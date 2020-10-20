package xyz.vopen.framework.neptune.core.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.AutoCloseableAsync;
import xyz.vopen.framework.neptune.common.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.configuration.JobManagerOptions;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.common.utils.ExecutorStUtil;
import xyz.vopen.framework.neptune.common.utils.ExecutorThreadFactory;
import xyz.vopen.framework.neptune.common.utils.ShutdownHookUtil;
import xyz.vopen.framework.neptune.core.autoconfigure.NeptuneServerAutoConfiguration;
import xyz.vopen.framework.neptune.core.dispatcher.DefaultDispatcherFactory;
import xyz.vopen.framework.neptune.core.dispatcher.Dispatcher;
import xyz.vopen.framework.neptune.core.exceptions.NeptuneEntrypointException;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcServiceUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link NeptuneEntrypoint} Neptune entry points.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public abstract class NeptuneEntrypoint implements AutoCloseableAsync, FatalErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(NeptuneEntrypoint.class);

  protected static final int STARTUP_FAILURE_RETURN_CODE = 1;
  protected static final int RUNTIME_FAILURE_RETURN_CODE = 2;
  private static final Time INITIALIZATION_SHUTDOWN_TIMEOUT = Time.seconds(30L);

  private final Object lock = new Object();
  private final Configuration configuration;
  private RpcService rpcService;
  private ExecutorService ioExecutor;
  private final Thread shutDownHook;
  private CompletableFuture<ApplicationStatus> terminationFuture;
  private final AtomicBoolean isShutDown = new AtomicBoolean(false);

  protected NeptuneEntrypoint(Configuration configuration) {
    this.configuration = configuration;
    this.terminationFuture = new CompletableFuture();
    this.shutDownHook =
        ShutdownHookUtil.addShutdownHook(
            this::cleanupDirectories, getClass().getSimpleName(), logger);
  }

  /**
   * Run Neptune application invoked by {@link NeptuneServerAutoConfiguration}.
   *
   * @param neptuneEntrypoint
   */
  public static void runApplicationEntrypoint(NeptuneEntrypoint neptuneEntrypoint) {
    final String simpleName = neptuneEntrypoint.getClass().getSimpleName();

    try {
      neptuneEntrypoint.startServer();
    } catch (NeptuneEntrypointException e) {
      logger.error(String.format("Could not start cluster entrypoint %s", simpleName), e);
      System.exit(STARTUP_FAILURE_RETURN_CODE);
    }

    neptuneEntrypoint
        .getTerminationFuture()
        .whenComplete(
            (applicationStatus, throwable) -> {
              final int returnCode;

              if (throwable != null) {
                returnCode = RUNTIME_FAILURE_RETURN_CODE;
              } else {
                returnCode = applicationStatus.processExitCode();
              }
              logger.info(
                  "Terminating Neptune entrypoint process {} with exit code {}.",
                  simpleName,
                  returnCode,
                  throwable);

              System.exit(returnCode);
            });
  }

  public void startServer() throws NeptuneEntrypointException {
    logger.info("Starting {}.", getClass().getSimpleName());
    try {
      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          runServer(configuration);
          return null;
        }
      }.call();
    } catch (Throwable throwable) {
      Throwable stripException =
          ExceptionUtil.stripException(throwable, UndeclaredThrowableException.class);

      try {
        shutDownAsync(
                ApplicationStatus.FAILED, ExceptionUtil.stringifyException(stripException), false)
            .get(INITIALIZATION_SHUTDOWN_TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        stripException.addSuppressed(e);
      }

      throw new NeptuneEntrypointException(
          "Failed to initialize the Neptune entrypoint", stripException);
    }
  }

  private void runServer(Configuration configuration) throws Exception {
    synchronized (lock) {
      initializerServices(configuration);

      Dispatcher dispatcher =
          DefaultDispatcherFactory.INSTANCE.create(configuration, this, rpcService);

      dispatcher.internalCallOnStart();

      dispatcher
          .getShutDownFuture()
          .whenComplete(
              (applicationStatus, throwable) -> {
                if (throwable != null) {
                  shutDownAsync(
                      ApplicationStatus.UNKNOWN,
                      ExceptionUtil.stringifyException(throwable),
                      false);
                } else {
                  shutDownAsync(applicationStatus, null, true);
                }
              });
    }
  }

  /**
   * Initializer services.
   *
   * @param configuration
   * @throws Exception Thrown when service initializer failed.
   */
  private void initializerServices(Configuration configuration) throws Exception {
    synchronized (lock) {
      this.rpcService =
          AkkaRpcServiceUtils.createRemoteRpcService(
              configuration,
              configuration.getString(JobManagerOptions.ADDRESS),
              String.valueOf(configuration.getInteger(JobManagerOptions.PORT)),
              configuration.getString(JobManagerOptions.BIND_HOST),
              configuration.getOptional(JobManagerOptions.RPC_BIND_PORT));

      this.ioExecutor =
          Executors.newFixedThreadPool(
              Runtime.getRuntime().availableProcessors(), new ExecutorThreadFactory("neptune-io"));
    }
  }

  /**
   * Asynchronous stop all services.
   *
   * @param applicationStatus
   * @param diagnostics
   * @param cleanupData
   * @return
   */
  private CompletableFuture<ApplicationStatus> shutDownAsync(
      ApplicationStatus applicationStatus, @Nullable String diagnostics, boolean cleanupData) {
    if (isShutDown.compareAndSet(false, true)) {
      logger.info(
          "Shutting {} down with application status {}. Diagnostics {}.",
          getClass().getSimpleName(),
          applicationStatus,
          diagnostics);

      final CompletableFuture<Void> serviceShutDownFuture = stopServices(cleanupData);

      serviceShutDownFuture.whenComplete(
          (Void ignored, Throwable serviceThrowable) -> {
            if (serviceThrowable != null) {
              terminationFuture.completeExceptionally(serviceThrowable);
            } else {
              terminationFuture.complete(applicationStatus);
            }
          });
    }
    return terminationFuture;
  }

  private CompletableFuture<Void> stopServices(boolean cleanupData) {
    synchronized (lock) {
      Throwable exception = null;

      final Collection<CompletableFuture<Void>> terminationFutures = new ArrayList<>(3);

      if (this.ioExecutor != null) {
        terminationFutures.add(
            ExecutorStUtil.nonBlockingShutdown(50L, TimeUnit.MILLISECONDS, ioExecutor));
      }

      if (this.rpcService != null) {
        terminationFutures.add(rpcService.stopService());
      }

      if (exception != null) {
        terminationFutures.add(FutureUtil.completedExceptionally(exception));
      }
      return FutureUtil.completeAll(terminationFutures);
    }
  }

  public CompletableFuture<ApplicationStatus> getTerminationFuture() {
    return this.terminationFuture;
  }

  /**
   * Clean up of temporary directories create by {@link NeptuneEntrypoint}.
   *
   * @throws IOException Thrown when the tmeporary directories could not be clean up.
   */
  private void cleanupDirectories() throws IOException {
    ShutdownHookUtil.removeShutdownHook(shutDownHook, getClass().getSimpleName(), logger);
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return shutDownAsync(
            ApplicationStatus.UNKNOWN, "Neptune entrypoints has been closed externally", true)
        .thenAccept(ignored -> {});
  }

  @Override
  public void onFatalError(Throwable exception) {
    logger.error("Fatal error occurred in the cluster entrypoint.", exception);
    System.exit(RUNTIME_FAILURE_RETURN_CODE);
  }
}

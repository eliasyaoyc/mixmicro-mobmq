package xyz.vopen.framework.neptune.core.jobmanager;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.common.utils.ExecutorThreadFactory;
import xyz.vopen.framework.neptune.common.utils.ShutdownHookUtil;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.configuration.JobManagerOptions;
import xyz.vopen.framework.neptune.core.dispatcher.*;
import xyz.vopen.framework.neptune.core.exceptions.JobManagerInitializeException;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;
import xyz.vopen.framework.neptune.core.rpc.akka.AkkaRpcService;
import xyz.vopen.framework.neptune.core.rpc.akka.AkkaRpcServiceUtils;
import xyz.vopen.framework.neptune.core.rpc.akka.AkkaUtils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link JobManagerRunner}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/11
 */
public class JobManagerRunner implements FatalErrorHandler, AutoCloseableAsync {
  private static final Logger logger = LoggerFactory.getLogger(JobManagerRunner.class);

  private static final Time INITIALIZATION_SHUTDOWN_TIMEOUT = Time.seconds(30L);

  @GuardedBy("lock")
  private final RpcService rpcService;

  @GuardedBy("lock")
  private DispatcherComponent component;

  private final JobManager jobManager;
  private final Time timeout;
  /** Executor used to run future callbacks. */
  private final ExecutorService ioExecutor;

  private final CompletableFuture<Void> terminationFuture;
  private final AtomicBoolean isShutDown = new AtomicBoolean(false);
  private final Configuration configuration;

  private final Thread shutDownHook;

  public JobManagerRunner(Configuration configuration) throws Exception {
    Preconditions.checkNotNull(configuration, "Configuration must not empty");

    this.timeout = AkkaUtils.getTimeoutAsTime(configuration);
    this.rpcService = createRpcService(configuration);
    this.ioExecutor =
        Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ExecutorThreadFactory("jobmanager-future"));
    this.terminationFuture = new CompletableFuture<>();
    this.jobManager = new JobManager();
    this.configuration = configuration;
    this.shutDownHook =
        ShutdownHookUtil.addShutdownHook(
            this::cleanupDirectories, getClass().getSimpleName(), logger);
  }

  /**
   * Create a RPC service for the Job manager.
   *
   * @param configuration for the JobManager.
   * @return {@link RpcService} instance.
   */
  RpcService createRpcService(Configuration configuration) throws Exception {
    Preconditions.checkNotNull(configuration);
    AkkaRpcService remoteRpcService =
        AkkaRpcServiceUtils.createRemoteRpcService(
            configuration,
            configuration.getString(JobManagerOptions.ADDRESS),
            getRpcPortRange(configuration),
            configuration.getString(JobManagerOptions.BIND_HOST),
            configuration.getOptional(JobManagerOptions.RPC_BIND_PORT));
    return remoteRpcService;
  }

  public void startJobManager() throws JobManagerInitializeException {
    logger.info("JobManager Starting {} .", getClass().getSimpleName());

    try {

      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          runJobManager(configuration);
          return null;
        }
      }.call();

    } catch (Throwable e) {
      final Throwable strippedThrowable =
          ExceptionUtil.stripException(e, UndeclaredThrowableException.class);

      try {
        shutdownAsync(
                ApplicationStatus.FAILED,
                ExceptionUtil.stringifyException(strippedThrowable),
                false)
            .get(INITIALIZATION_SHUTDOWN_TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException t) {
        strippedThrowable.addSuppressed(t);
      }
      throw new JobManagerInitializeException(
          String.format("Failed to initialize the Job Manager %s,", getClass().getSimpleName()),
          strippedThrowable);
    }
  }

  @GuardedBy("lock")
  private void runJobManager(Configuration configuration) throws Exception {

    // write host information into configuration.
    configuration.setString(JobManagerOptions.ADDRESS, rpcService.getAddress());
    configuration.setInteger(JobManagerOptions.PORT, rpcService.getPort());

    // create DispatcherComponentFactory.
    final DispatcherComponentFactory dispatcherComponentFactory =
        createDispatcherComponentFactory(configuration);

    // create DispatcherComponent via DispatcherComponentFactory and start.
    component = dispatcherComponentFactory.create(configuration, ioExecutor, rpcService, this);

    component
        .getShutDownFuture()
        .whenComplete(
            (applicationStatus, throwable) -> {
              if (throwable != null) {
                shutdownAsync(
                    ApplicationStatus.UNKNOWN, ExceptionUtil.stringifyException(throwable), false);
              } else {
                // This is the general shutdown path. If a separate more specific shutdown was
                // already triggered,this will do nothing.
                shutdownAsync(applicationStatus, null, true);
              }
            });
  }

  private DispatcherComponentFactory createDispatcherComponentFactory(
      final Configuration configuration) {
    return new DefaultDispatcherComponentFactory(
        new DefaultDispatcherRunnerFactory(
            DefaultDispatcherLeaderProcessFactoryFactory.create(
                configuration, SessionDispatcherFactory.INSTANCE)));
  }

  /**
   * Returns the port range for the common {@link RpcService}.
   *
   * @param configuration to extract the port range from.
   * @return Port range for the common {@link RpcService}.
   */
  private String getRpcPortRange(Configuration configuration) {
    return String.valueOf(configuration.getInteger(JobManagerOptions.PORT));
  }

  /**
   * Clean up of temporary directories created by the {@link JobManager}.
   *
   * @throws IOException if the temporary directories could not be cleaned up.
   */
  private void cleanupDirectories() throws IOException {}

  // ===================== Lifecycle management =====================

  public void start() throws Exception {
    this.jobManager.start();
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return shutdownAsync(
            ApplicationStatus.UNKNOWN, "JobManager entrypoint has been closed externally", true)
        .thenAccept(ignored -> {});
  }

  private CompletableFuture<Void> shutdownAsync(
      ApplicationStatus applicationStatus, @Nullable String diagnostics, boolean cleanupHaData) {
    return null;
  }

  @Override
  public void onFatalError(Throwable exception) {}
}

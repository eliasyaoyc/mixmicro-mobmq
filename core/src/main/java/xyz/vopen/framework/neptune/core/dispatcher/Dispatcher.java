package xyz.vopen.framework.neptune.core.dispatcher;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.core.exceptions.DispatcherException;
import xyz.vopen.framework.neptune.core.jobmanager.JobManager;
import xyz.vopen.framework.neptune.core.persistence.Persistence;
import xyz.vopen.framework.neptune.core.persistence.PersistenceFactory;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.rpc.RpcService;
import xyz.vopen.framework.neptune.rpc.message.Acknowledge;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * {@link Dispatcher} Base class for the Dispatcher component. The Dispatcher component is
 * responsible for receiving job submissions,persisting them, spawning {@link JobManager} to execute
 * the jobs and to recover them in case of a master failure. Furthermore, it knows the state of the
 * Neptune session cluster.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public abstract class Dispatcher extends RpcEndpoint implements DispatcherGateway {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Configuration configuration;
  private final RpcService rpcService;
  private final String dispatcherId;
  private final @Nonnull Persistence persistence;
  private final FatalErrorHandler fatalErrorHandler;
  protected final CompletableFuture<ApplicationStatus> shutDownFuture;

  public Dispatcher(
      final Configuration configuration,
      final FatalErrorHandler fatalErrorHandler,
      final RpcService rpcService,
      final String dispatcherId) {
    super(rpcService, dispatcherId);
    Preconditions.checkNotNull(configuration, "Configuration is null");

    this.configuration = configuration;
    this.rpcService = rpcService;
    this.fatalErrorHandler = fatalErrorHandler;
    this.dispatcherId = dispatcherId;
    // TODO: Persistence.PersistenceEnum.MONGO will changed that obtain from Configuration.
    this.persistence =
        PersistenceFactory.INSTANCE.create(configuration, Persistence.PersistenceEnum.MONGO);
    this.shutDownFuture = new CompletableFuture<>();
  }

  // =====================   Lifecycle methods  =====================
  @Override
  protected void onStart() throws Exception {
    try {
      startDispatcherServices();
    } catch (Exception e) {
      DispatcherException ex =
          new DispatcherException(
              String.format("Could not start the Dispatcher %s", getAddress(), e));
      onFatalError(ex);
      throw ex;
    }
  }

  private void startDispatcherServices() throws Exception {
    try {

    } catch (Exception e) {
      handleStartDispatcherServicesException(e);
    }
  }

  private void handleStartDispatcherServicesException(Exception e) throws Exception {
    try {
      stopDispatcherServices();
    } catch (Exception exception) {
      e.addSuppressed(exception);
    }
    throw e;
  }

  private void stopDispatcherServices() throws Exception {
    Exception exception = null;
    try {

    } catch (Exception e) {
      exception = e;
    }
    ExceptionUtil.tryRethrowException(exception);
  }

  @Override
  protected CompletableFuture<Void> onStop() {
    logger.info("Stopping dispatcher {} .", getAddress());
    return super.onStop();
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return super.closeAsync();
  }

  @Override
  public CompletableFuture<Acknowledge> submitJob(Time timeout) {
    return null;
  }

  @Override
  public CompletableFuture<List<String>> listJobs(Time timeout) {
    return null;
  }

  @Override
  public CompletableFuture<Acknowledge> shutdownJobManager() {
    return null;
  }

  protected void onFatalError(Throwable throwable) {
    fatalErrorHandler.onFatalError(throwable);
  }

  /**
   * Returns a future that indicates the status of application.
   *
   * @return a future that indicates the status of application.
   */
  public CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return this.shutDownFuture;
  }
}

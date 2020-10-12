package xyz.vopen.framework.neptune.core.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link DispatcherComponent} Component which starts a {@link Dispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DispatcherComponent implements AutoCloseableAsync {
  private static final Logger logger = LoggerFactory.getLogger(DispatcherComponent.class);

  private final @Nonnull DispatcherRunner dispatcherRunner;
  private final CompletableFuture<Void> terminationFuture;
  private final CompletableFuture<ApplicationStatus> shutDownFuture;
  private final AtomicBoolean isRunning = new AtomicBoolean(true);

  DispatcherComponent(@Nonnull DispatcherRunner dispatcherRunner) {
    this.dispatcherRunner = dispatcherRunner;
    this.terminationFuture = new CompletableFuture<>();
    this.shutDownFuture = new CompletableFuture<>();
    registerShutDownFuture();
  }

  private void registerShutDownFuture() {
    FutureUtil.forward(dispatcherRunner.getShutDownFuture(), shutDownFuture);
  }

  public final CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return this.shutDownFuture;
  }

  /**
   * Deregister the Neptune application from the resource management system by signalling {@link
   * xyz.vopen.framework.neptune.core.jobmanager.JobManager}.
   *
   * @param applicationStatus to terminate the application with.
   * @param diagnostics additional information about the shut down, can be {@code null}.
   * @return Future which is completed once the shut down.
   */
  public CompletableFuture<Void> deregisterApplicationAndClose(
      final ApplicationStatus applicationStatus, final @Nullable String diagnostics) {
    return null;
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return null;
  }
}

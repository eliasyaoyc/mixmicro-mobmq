package xyz.vopen.framework.neptune.core.dispatcher.runner;

import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;

import java.util.concurrent.CompletableFuture;

/**
 * {@link DispatcherRunner} The {@link DispatcherRunner} encapsulates how a {@link
 * xyz.vopen.framework.neptune.core.dispatcher.Dispatcher} is being executed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherRunner extends AutoCloseableAsync {

  /**
   * Returns shut down future of this runner. The shut down future is being completed with the final
   * {@link ApplicationStatus} once the runner wants to shut down.
   *
   * @return future which the final Job state.
   */
  CompletableFuture<ApplicationStatus> getShutDownFuture();
}

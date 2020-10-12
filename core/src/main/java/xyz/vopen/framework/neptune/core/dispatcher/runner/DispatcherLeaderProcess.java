package xyz.vopen.framework.neptune.core.dispatcher.runner;

import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.core.dispatcher.DispatcherGateway;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DispatcherLeaderProcess} Leader process which encapsulates the lifecycle of the {@link
 * xyz.vopen.framework.neptune.core.dispatcher.Dispatcher} component.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherLeaderProcess extends AutoCloseableAsync {

  void start();

  UUID getLeaderSessionId();

  CompletableFuture<DispatcherGateway> getDispatcherGateway();

  CompletableFuture<String> getLeaderAddressFuture();

  CompletableFuture<ApplicationStatus> getShutDownFuture();
}

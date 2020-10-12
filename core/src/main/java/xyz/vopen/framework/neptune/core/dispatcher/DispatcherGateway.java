package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.core.rpc.RpcTimeout;
import xyz.vopen.framework.neptune.core.rpc.message.Acknowledge;
import xyz.vopen.framework.neptune.core.web.RestfulGateway;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DispatcherGateway} Gateway for the Dispatcher component.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherGateway extends RestfulGateway {

  /**
   * Submit a job to the dispatcher
   *
   * @param timeout RPC timeout.
   * @return A future acknowledge if the submission succeeded.
   */
  CompletableFuture<Acknowledge> submitJob(@RpcTimeout Time timeout);

  /**
   * Returns list the current set of submitted jobs.
   *
   * @param timeout RPC timeout.
   * @return A future list of currently submitted jobs.
   */
  CompletableFuture<List<String>> listJobs(@RpcTimeout Time timeout);

  default CompletableFuture<Acknowledge> shutdownJobManager() {
    return shutdownJobManager();
  }
}

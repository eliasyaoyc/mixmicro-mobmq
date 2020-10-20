package xyz.vopen.framework.neptune.rpc;

import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.rpc.message.Acknowledge;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DispatcherGateway} Gateway for the Dispatcher server component.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherGateway extends RpcGateway {

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

  /**
   * @param jobId unique identity of job.
   * @param timout RPC timeout.
   * @param removed Physical deleted the job if it is true.
   * @return A future acknowledge if the submission succeeded.
   */
  CompletableFuture<Acknowledge> stopJob(String jobId, @RpcTimeout Time timout, boolean removed);

  /**
   * Shut down the {@code JobManager}.
   *
   * @return A future acknowledge.
   */
  CompletableFuture<Acknowledge> shutdownJobManager();
}

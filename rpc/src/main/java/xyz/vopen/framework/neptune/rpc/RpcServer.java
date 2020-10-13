package xyz.vopen.framework.neptune.rpc;

import java.util.concurrent.CompletableFuture;

/**
 * {@link RpcServer}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public interface RpcServer extends RpcStartStoppable, MainThreadExecutable, RpcGateway {

  /**
   * Returns a future which is completed when the rpc endpoint has been terminated.
   *
   * @return Future indicating when the rpc endpoint has been terminated.
   */
  CompletableFuture<Void> getTerminationFuture();
}

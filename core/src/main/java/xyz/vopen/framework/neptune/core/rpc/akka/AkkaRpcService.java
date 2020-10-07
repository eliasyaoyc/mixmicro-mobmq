package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.core.concurrent.ScheduledExecutor;
import xyz.vopen.framework.neptune.core.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.core.rpc.RpcGateway;
import xyz.vopen.framework.neptune.core.rpc.RpcServer;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.*;

/**
 * {@link AkkaRpcService} Based {@link RpcService} implementation. The RPC service starts an Akka
 * actor to receive RPC invocation from a {@link RpcGateway}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
@ThreadSafe
public class AkkaRpcService implements RpcService {
  private static final Logger logger = LoggerFactory.getLogger(AkkaRpcService.class);

  @VisibleForTesting
  public AkkaRpcService(
      final ActorSystem actorSystem, final AkkaRpcServiceConfiguration configuration) {}

  @Override
  public String getAddress() {
    return null;
  }

  @Override
  public int getPort() {
    return 0;
  }

  @Override
  public <C extends RpcGateway> CompletableFuture<C> connect(String address, Class<C> clazz) {
    return null;
  }

  @Override
  public <C extends RpcEndpoint & RpcGateway> RpcServer startServer(C rpcEndpoint) {
    return null;
  }

  @Override
  public void stopServer(RpcServer selfGateway) {}

  @Override
  public CompletableFuture<Void> stopService() {
    return null;
  }

  @Override
  public CompletableFuture<Void> getTerminationFuture() {
    return null;
  }

  @Override
  public Executor getExecutor() {
    return null;
  }

  @Override
  public ScheduledExecutor getScheduledExecutor() {
    return null;
  }

  @Override
  public ScheduledFuture<?> scheduleRunnable(Runnable runnable, long delay, TimeUnit unit) {
    return null;
  }

  @Override
  public void execute(Runnable runnable) {}

  @Override
  public <T> CompletableFuture<T> execute(Callable<T> callable) {
    return null;
  }
}

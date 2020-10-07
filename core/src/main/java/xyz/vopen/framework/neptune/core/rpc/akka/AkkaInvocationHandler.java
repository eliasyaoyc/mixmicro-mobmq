package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.ActorRef;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.core.rpc.RpcServer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * {@link AkkaInvocationHandler} Invocation handler to be used with a {@link AkkaRpcActor}. The
 * invocation handler wraps the rpc in a {@link
 * xyz.vopen.framework.neptune.core.rpc.message.LocalRpcInvocation} message and then sends it to the
 * {@link AkkaRpcActor} where is it executed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaInvocationHandler implements InvocationHandler, AkkaBasedEndpoint, RpcServer {
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return null;
  }

  @Override
  public ActorRef getActorRef() {
    return null;
  }

  @Override
  public CompletableFuture<Void> getTerminationFuture() {
    return null;
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

  @Override
  public void runAsync(Runnable runnable) {}

  @Override
  public <V> CompletableFuture<V> callAsync(Callable<V> callable, Time callTimeout) {
    return null;
  }

  @Override
  public void scheduleRunAsync(Runnable runnable, long delay) {}

  @Override
  public String getAddress() {
    return null;
  }

  @Override
  public String getHostname() {
    return null;
  }
}

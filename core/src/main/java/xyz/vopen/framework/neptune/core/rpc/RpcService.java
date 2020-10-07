package xyz.vopen.framework.neptune.core.rpc;

import xyz.vopen.framework.neptune.core.concurrent.ScheduledExecutor;
import xyz.vopen.framework.neptune.core.exceptions.rpc.RpcConnectionException;

import java.io.Serializable;
import java.util.concurrent.*;

/**
 * {@link RpcService} Interface for rpc services. An rpc service is used to start and connect to a
 * {@link RpcEndpoint}. Connecting to a rpc server will return a {@link RpcGateway} which can be
 * used to call remote procedures.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public interface RpcService {

  /**
   * Return the hostname or host address under which the rpc service can be reached. If the rpc
   * service cannot be contacted remotely, then it will return an empty string.
   *
   * @return Address of the rpc service or empty string if local rpc service
   */
  String getAddress();

  /**
   * Return the port under which the rpc service is reachable. If the rpc service cannot be
   * contacted remotely, then it will return -1.
   *
   * @return Port of the rpc service or -1 if local rpc service
   */
  int getPort();

  /**
   * Connect to a remote rpc server under the provided address. Returns a rpc gateway which can be
   * used to communicate with the rpc server. If the connection failed, then the returned future is
   * failed with a {@link RpcConnectionException}.
   *
   * @param address Address of the remote rpc server
   * @param clazz Class of the rpc gateway to return
   * @param <C> Type of the rpc gateway to return
   * @return Future containing the rpc gateway or an {@link RpcConnectionException} if the
   *     connection attempt failed
   */
  <C extends RpcGateway> CompletableFuture<C> connect(String address, Class<C> clazz);

  /**
   * Start a rpc server which forwards the remote procedure calls to the provided rpc endpoint.
   *
   * @param rpcEndpoint Rpc protocol to dispatch the rpcs to
   * @param <C> Type of the rpc endpoint
   * @return Self gateway to dispatch remote procedure calls to oneself
   */
  <C extends RpcEndpoint & RpcGateway> RpcServer startServer(C rpcEndpoint);

  /**
   * Stop the underlying rpc server of the provided self gateway.
   *
   * @param selfGateway Self gateway describing the underlying rpc server
   */
  void stopServer(RpcServer selfGateway);

  /**
   * Trigger the asynchronous stopping of the {@link RpcService}.
   *
   * @return Future which is completed once the {@link RpcService} has been fully stopped.
   */
  CompletableFuture<Void> stopService();

  /**
   * Returns a future indicating when the RPC service has been shut down.
   *
   * @return Termination future
   */
  CompletableFuture<Void> getTerminationFuture();

  /**
   * Gets the executor, provided by this RPC service. This executor can be used for example for the
   * {@code handleAsync(...)} or {@code thenAcceptAsync(...)} methods of futures.
   *
   * <p><b>IMPORTANT:</b> This executor does not isolate the method invocations against any
   * concurrent invocations and is therefore not suitable to run completion methods of futures that
   * modify state of an {@link RpcEndpoint}. For such operations, one needs to use the {@link
   * RpcEndpoint#getMainThreadExecutor() MainThreadExecutionContext} of that {@code RpcEndpoint}.
   *
   * @return The execution context provided by the RPC service
   */
  Executor getExecutor();

  /**
   * Gets a scheduled executor from the RPC service. This executor can be used to schedule tasks to
   * be executed in the future.
   *
   * <p><b>IMPORTANT:</b> This executor does not isolate the method invocations against any
   * concurrent invocations and is therefore not suitable to run completion methods of futures that
   * modify state of an {@link RpcEndpoint}. For such operations, one needs to use the {@link
   * RpcEndpoint#getMainThreadExecutor() MainThreadExecutionContext} of that {@code RpcEndpoint}.
   *
   * @return The RPC service provided scheduled executor
   */
  ScheduledExecutor getScheduledExecutor();

  /**
   * Execute the runnable in the execution context of this RPC Service, as returned by {@link
   * #getExecutor()}, after a scheduled delay.
   *
   * @param runnable Runnable to be executed
   * @param delay The delay after which the runnable will be executed
   */
  ScheduledFuture<?> scheduleRunnable(Runnable runnable, long delay, TimeUnit unit);

  /**
   * Execute the given runnable in the executor of the RPC service. This method can be used to run
   * code outside of the main thread of a {@link RpcEndpoint}.
   *
   * <p><b>IMPORTANT:</b> This executor does not isolate the method invocations against any
   * concurrent invocations and is therefore not suitable to run completion methods of futures that
   * modify state of an {@link RpcEndpoint}. For such operations, one needs to use the {@link
   * RpcEndpoint#getMainThreadExecutor() MainThreadExecutionContext} of that {@code RpcEndpoint}.
   *
   * @param runnable to execute
   */
  void execute(Runnable runnable);

  /**
   * Execute the given callable and return its result as a {@link CompletableFuture}. This method
   * can be used to run code outside of the main thread of a {@link RpcEndpoint}.
   *
   * <p><b>IMPORTANT:</b> This executor does not isolate the method invocations against any
   * concurrent invocations and is therefore not suitable to run completion methods of futures that
   * modify state of an {@link RpcEndpoint}. For such operations, one needs to use the {@link
   * RpcEndpoint#getMainThreadExecutor() MainThreadExecutionContext} of that {@code RpcEndpoint}.
   *
   * @param callable to execute
   * @param <T> is the return value type
   * @return Future containing the callable's future result
   */
  <T> CompletableFuture<T> execute(Callable<T> callable);
}

package xyz.vopen.framework.neptune.rpc;

import xyz.vopen.framework.neptune.common.utils.time.Time;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * {@link MainThreadExecutable} Interface to execute {@link Runnable} and {@link
 * java.util.concurrent.Callable} in the main thread of the underlying RPC endpoint.
 *
 * <p>This interface is intended to be implemented by the self gateway in a {@link RpcEndpoint}
 * implementation which allows to dispatch local procedures to the main thread of the underlying RPC
 * endpoint.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public interface MainThreadExecutable {

  /**
   * Execute the runnable in the main thread of the underlying RPC endpoint.
   *
   * @param runnable Runnable to executed.
   */
  void runAsync(Runnable runnable);

  /**
   * Execute the callable in the main thread of the underlying RPC endpoint and return a future for
   * the callable result. If the future is not completed within the given timeout, the returned
   * future will throw a {@link java.util.concurrent.TimeoutException}.
   *
   * @param callable Callable to be executed.
   * @param callTimeout Timeout fot the future to complete.
   * @param <V> Return value of the callable.
   * @return Future of the callable result.
   */
  <V> CompletableFuture<V> callAsync(Callable<V> callable, Time callTimeout);

  /**
   * Execute the runnable in the main thread of the underlying RPC endpoint, which a delay of the
   * given number of milliseconds.
   *
   * @param runnable Runnable to be executed.
   * @param delay The delay,in milliseconds, after which the runnable will be executed.
   */
  void scheduleRunAsync(Runnable runnable, long delay);
}

package xyz.vopen.framework.neptune.core.rpc;

import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * {@link RpcUtil} Utility functions for Neptune RPC implementation.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public class RpcUtil {

  // We don't want this class to be instantiable
  private RpcUtil() {}

  /**
   * <b>HACK:</b> Set to 21474835 seconds, Akka's maximum delay (Akka 2.4.20). The value cannot be
   * higher or an {@link IllegalArgumentException} will be thrown during an RPC. Check the private
   * method {@code checkMaxDelay()} in {@link akka.actor.LightArrayRevolverScheduler}.
   */
  public static final Time INF_TIMEOUT = Time.seconds(21474835);

  public static final Duration INF_DURATION = Duration.ofSeconds(21474835);

  /**
   * Extracts all {@link RpcGateway} interfaces implemented by the given clazz.
   *
   * @param clazz from which to extract the implemented RpcGateway interfaces
   * @return A set of all implemented RpcGateway interfaces
   */
  public static Set<Class<? extends RpcGateway>> extractImplementedRpcGateways(Class<?> clazz) {
    HashSet<Class<? extends RpcGateway>> interfaces = new HashSet<>();

    while (clazz != null) {
      for (Class<?> interfaze : clazz.getInterfaces()) {
        if (RpcGateway.class.isAssignableFrom(interfaze)) {
          interfaces.add((Class<? extends RpcGateway>) interfaze);
        }
      }

      clazz = clazz.getSuperclass();
    }

    return interfaces;
  }

  /**
   * Shuts the given {@link RpcEndpoint} down and awaits its termination.
   *
   * @param rpcEndpoint to terminate
   * @param timeout for this operation
   * @throws ExecutionException if a problem occurred
   * @throws InterruptedException if the operation has been interrupted
   * @throws TimeoutException if a timeout occurred
   */
  public static void terminateRpcEndpoint(RpcEndpoint rpcEndpoint, Time timeout)
      throws ExecutionException, InterruptedException, TimeoutException {
    rpcEndpoint.closeAsync().get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
  }

  /**
   * Shuts the given {@link RpcEndpoint RpcEndpoints} down and waits for their termination.
   *
   * @param rpcEndpoints to shut down
   * @param timeout for this operation
   * @throws InterruptedException if the operation has been interrupted
   * @throws ExecutionException if a problem occurred
   * @throws TimeoutException if a timeout occurred
   */
  public static void terminateRpcEndpoints(Time timeout, RpcEndpoint... rpcEndpoints)
      throws InterruptedException, ExecutionException, TimeoutException {
    terminateAsyncCloseables(Arrays.asList(rpcEndpoints), timeout);
  }

  /**
   * Shuts the given rpc service down and waits for its termination.
   *
   * @param rpcService to shut down
   * @param timeout for this operation
   * @throws InterruptedException if the operation has been interrupted
   * @throws ExecutionException if a problem occurred
   * @throws TimeoutException if a timeout occurred
   */
  public static void terminateRpcService(RpcService rpcService, Time timeout)
      throws InterruptedException, ExecutionException, TimeoutException {
    rpcService.stopService().get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
  }

  /**
   * Shuts the given rpc services down and waits for their termination.
   *
   * @param rpcServices to shut down
   * @param timeout for this operation
   * @throws InterruptedException if the operation has been interrupted
   * @throws ExecutionException if a problem occurred
   * @throws TimeoutException if a timeout occurred
   */
  public static void terminateRpcServices(Time timeout, RpcService... rpcServices)
      throws InterruptedException, ExecutionException, TimeoutException {
    terminateAsyncCloseables(
        Arrays.stream(rpcServices)
            .map(rpcService -> (AutoCloseableAsync) rpcService::stopService)
            .collect(Collectors.toList()),
        timeout);
  }

  private static void terminateAsyncCloseables(
      Collection<? extends AutoCloseableAsync> closeables, Time timeout)
      throws InterruptedException, ExecutionException, TimeoutException {
    final Collection<CompletableFuture<?>> terminationFutures = new ArrayList<>(closeables.size());

    for (AutoCloseableAsync closeableAsync : closeables) {
      if (closeableAsync != null) {
        terminationFutures.add(closeableAsync.closeAsync());
      }
    }

    FutureUtil.waitForAll(terminationFutures).get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
  }

  /**
   * Returns the hostname onto which the given {@link RpcService} has been bound. If the {@link
   * RpcService} has been started in local mode, then the hostname is {@code "hostname"}.
   *
   * @param rpcService to retrieve the hostname for
   * @return hostname onto which the given {@link RpcService} has been bound or localhost
   */
  public static String getHostname(RpcService rpcService) {
    final String rpcServiceAddress = rpcService.getAddress();
    return rpcServiceAddress != null && rpcServiceAddress.isEmpty()
        ? "localhost"
        : rpcServiceAddress;
  }
}

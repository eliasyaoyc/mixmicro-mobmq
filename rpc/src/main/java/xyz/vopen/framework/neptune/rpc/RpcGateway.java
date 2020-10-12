package xyz.vopen.framework.neptune.rpc;

/**
 * {@link RpcGateway} Rpc gateway interface which has to be implemented by Rpc gateways.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public interface RpcGateway {

  /**
   * Returns the fully qualified address under which the associated rpc endpoint is reachable.
   *
   * @return Fully qualified (RPC) address under which the associated rpc endpoint is reachable.
   */
  String getAddress();

  /***
   * Returns the fully qualified hostname under which the associated rpc endpoint is reachable.
   * @return Fully qualified hostname under which the associated rpc endpoint is reachable.
   */
  String getHostname();
}

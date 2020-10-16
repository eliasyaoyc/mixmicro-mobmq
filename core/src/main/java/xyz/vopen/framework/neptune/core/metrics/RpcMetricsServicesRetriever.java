package xyz.vopen.framework.neptune.core.metrics;

import xyz.vopen.framework.neptune.rpc.RpcService;

import java.util.concurrent.CompletableFuture;

/**
 * {@link RpcMetricsServicesRetriever} implementation for rpc based {@link MetricService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/16
 */
public class RpcMetricsServicesRetriever implements MetricServiceRetriever {

  private RpcService rpcService;

  public RpcMetricsServicesRetriever(RpcService rpcService) {
    this.rpcService = rpcService;
  }

  @Override
  public CompletableFuture<MetricServiceGateway> retrieveService(String rpcServiceAddress) {
    return this.rpcService.connect(rpcServiceAddress, MetricServiceGateway.class);
  }
}

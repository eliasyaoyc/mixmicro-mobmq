package xyz.vopen.framework.neptune.core.metrics;

import java.util.concurrent.CompletableFuture;

/**
 * {@link MetricServiceRetriever}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/16
 */
public interface MetricServiceRetriever {

  /**
   * Retrieves for the given service address a {@link MetricServiceGateway}.
   *
   * @param rpcServiceAddress
   * @return
   */
  CompletableFuture<MetricServiceGateway> retrieveService(String rpcServiceAddress);
}

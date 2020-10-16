package xyz.vopen.framework.neptune.core.metrics;

import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.rpc.RpcGateway;
import xyz.vopen.framework.neptune.rpc.RpcTimeout;

import java.util.concurrent.CompletableFuture;

/**
 * {@link MetricServiceGateway} rpc gateway interface.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/16
 */
public interface MetricServiceGateway extends RpcGateway {
  CompletableFuture<MetricResult> queryMetrics(@RpcTimeout Time timeout);
}

package xyz.vopen.framework.neptune.core.highavailability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.RpcService;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * {@link HighAvailabilityService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/4
 */
public class HighAvailabilityService implements HighAvailability {
  private static final Logger LOG = LoggerFactory.getLogger(HighAvailabilityService.class);

  private final @Nonnull Configuration configuration;
  private final RpcService rpcService;

  private HighAvailabilityService(
      final @Nonnull Configuration configuration, final @Nonnull RpcService rpcService) {
    this.configuration = configuration;
    this.rpcService = rpcService;
  }

  public static HighAvailabilityService createFromConfiguration(
      final @Nonnull Configuration configuration, final @Nonnull RpcService rpcService) {
    return new HighAvailabilityService(configuration, rpcService);
  }

  public void start() {}

  @Override
  public CompletableFuture<Void> stop() {
    return null;
  }
}

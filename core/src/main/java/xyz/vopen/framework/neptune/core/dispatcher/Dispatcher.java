package xyz.vopen.framework.neptune.core.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.core.rpc.RpcService;
import xyz.vopen.framework.neptune.core.rpc.akka.AkkaRpcServiceUtils;
import xyz.vopen.framework.neptune.core.rpc.message.Acknowledge;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * {@link Dispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class Dispatcher extends RpcEndpoint implements DispatcherGateway {
  private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

  public static final String DISPATCHER_NAME = "dispatcher";
  private final @Nonnull Configuration configuration;

  protected final CompletableFuture<ApplicationStatus> shutDownFuture;

  public Dispatcher(
      Configuration configuration,
      RpcService rpcService,
      String endpointId,
      DispatcherServices dispatcherServices) {
    super(rpcService, AkkaRpcServiceUtils.createRandomName(endpointId));
    this.configuration = configuration;
    this.shutDownFuture = new CompletableFuture();
  }

  public CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return this.shutDownFuture;
  }

  // ============ Lifecycle ===================

  @Override
  protected void onStart() throws Exception {
    super.onStart();
  }

  @Override
  protected CompletableFuture<Void> onStop() {
    return super.onStop();
  }

  // ============ RPCs ===================

  @Override
  public CompletableFuture<Acknowledge> submitJob(Time timeout) {
    return null;
  }

  @Override
  public CompletableFuture<List<String>> listJobs(Time timeout) {
    return null;
  }
}

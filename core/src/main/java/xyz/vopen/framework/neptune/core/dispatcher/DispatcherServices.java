package xyz.vopen.framework.neptune.core.dispatcher;

import akka.actor.ActorSystem;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.RpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcServiceConfiguration;
import xyz.vopen.framework.neptune.rpc.akka.AkkaUtils;

/**
 * {@link DispatcherServices}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class DispatcherServices {
  private static final Logger logger = LoggerFactory.getLogger(DispatcherServices.class);

  private final Configuration configuration;
  private final RpcService rpcService;

  DispatcherServices(Configuration configuration) {
    Preconditions.checkNotNull(configuration, "Configuration empty.");
    this.configuration = configuration;
    ActorSystem actorSystem = AkkaUtils.createDefaultActorSystem();
    this.rpcService =
        new AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.defaultConfiguration());
  }

  // =====================  GETTER  =====================
  public Configuration getConfiguration() {
    return this.configuration;
  }

  public RpcService getRpcService() {
    return this.rpcService;
  }
}

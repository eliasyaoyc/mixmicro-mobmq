package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import javax.annotation.concurrent.ThreadSafe;

/**
 * {@link AkkaRpcService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
@ThreadSafe
public class AkkaRpcService implements RpcService {
  private static final Logger logger = LoggerFactory.getLogger(AkkaRpcService.class);


  @VisibleForTesting
  public AkkaRpcService(final ActorSystem actorSystem,final AkkaRpcServiceConfiguration configuration){

  }
}

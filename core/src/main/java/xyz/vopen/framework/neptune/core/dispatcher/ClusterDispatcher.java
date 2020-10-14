package xyz.vopen.framework.neptune.core.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;

/**
 * {@link ClusterDispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class ClusterDispatcher extends Dispatcher {
  private static final Logger logger = LoggerFactory.getLogger(ClusterDispatcher.class);

  ClusterDispatcher(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService,
      String dispatcherId) {
    super(configuration, fatalErrorHandler, rpcService, dispatcherId);
  }
}

package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;

/**
 * {@link StandaloneDispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class StandaloneDispatcher extends Dispatcher {

  StandaloneDispatcher(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService) {
    super(configuration, fatalErrorHandler, rpcService);
  }
}

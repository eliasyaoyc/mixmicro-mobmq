package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

/**
 * {@link DispatcherFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherFactory {

  Dispatcher createDispatcher(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService,
      String dispatcherId)
      throws Exception;
}

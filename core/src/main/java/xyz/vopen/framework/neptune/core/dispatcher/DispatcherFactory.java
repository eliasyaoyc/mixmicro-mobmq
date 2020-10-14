package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;

/**
 * {@link DispatcherFactory} Dispatcher factory interface.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherFactory {

  /**
   * Create a {@link Dispatcher}.
   *
   * @param configuration
   * @param rpcService
   * @return
   * @throws Exception
   */
  Dispatcher create(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService)
      throws Exception;
}

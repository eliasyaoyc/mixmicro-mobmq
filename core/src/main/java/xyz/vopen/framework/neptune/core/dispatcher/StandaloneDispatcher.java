package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

/**
 * {@link StandaloneDispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class StandaloneDispatcher extends Dispatcher {
  public StandaloneDispatcher(
      Configuration configuration,
      RpcService rpcService,
      String endpointId,
      DispatcherServices dispatcherServices) {
    super(configuration, rpcService, endpointId, dispatcherServices);
  }
}

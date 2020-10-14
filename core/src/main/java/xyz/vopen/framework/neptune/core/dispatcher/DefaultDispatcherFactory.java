package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;

/**
 * {@link DefaultDispatcherFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public enum DefaultDispatcherFactory implements DispatcherFactory {
  INSTANCE;

  @Override
  public Dispatcher create(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService,
      String dispatcherId) {
//    String[] addresses = configuration.getString(JobManagerOptions.ADDRESS).split(",");
//    if (addresses != null && addresses.length > 1) {
//      return new ClusterDispatcher(configuration, fatalErrorHandler, rpcService, dispatcherId);
//    } else {
      return new StandaloneDispatcher(configuration, fatalErrorHandler, rpcService, dispatcherId);
//    }
  }
}

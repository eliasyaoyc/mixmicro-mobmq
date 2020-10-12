package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import java.util.concurrent.Executor;

/**
 * {@link DispatcherComponentFactory} Factory for the {@link DispatcherComponent}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherComponentFactory {

  DispatcherComponent create(
      Configuration configuration,
      Executor ioExecutor,
      RpcService rpcService,
      FatalErrorHandler fatalErrorHandler)
      throws Exception;

}

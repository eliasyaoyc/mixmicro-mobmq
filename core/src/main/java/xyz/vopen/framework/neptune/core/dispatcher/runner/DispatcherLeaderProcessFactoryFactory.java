package xyz.vopen.framework.neptune.core.dispatcher.runner;

import xyz.vopen.framework.neptune.common.annoations.Internal;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import java.util.concurrent.Executor;

/**
 * {@link DispatcherLeaderProcessFactoryFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
@Internal
public interface DispatcherLeaderProcessFactoryFactory {

  DispatcherLeaderProcessFactory createFactory(
      Executor ioExecutor, RpcService rpcService, FatalErrorHandler fatalErrorHandler);
}

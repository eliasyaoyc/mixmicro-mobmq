package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherRunner;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderElectionService;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import java.util.concurrent.Executor;

/**
 * {@link DispatcherRunnerFactory} Factory interface for the {@link
 * xyz.vopen.framework.neptune.core.dispatcher.Dispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface DispatcherRunnerFactory {

  DispatcherRunner createDispatcherRunner(
      LeaderElectionService leaderElectionService,
      FatalErrorHandler fatalErrorHandler,
      Executor ioExecutor,
      RpcService rpcService) throws Exception;
}

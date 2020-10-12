package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.dispatcher.runner.DefaultDispatcherRunner;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherLeaderProcessFactory;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherLeaderProcessFactoryFactory;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherRunner;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderElectionService;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import java.util.concurrent.Executor;

/**
 * {@link DefaultDispatcherRunnerFactory} implementation which creates {@link
 * xyz.vopen.framework.neptune.core.dispatcher.runner.DefaultDispatcherRunner} instance.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DefaultDispatcherRunnerFactory implements DispatcherRunnerFactory {

  private final DispatcherLeaderProcessFactoryFactory dispatcherLeaderProcessFactoryFactory;

  public DefaultDispatcherRunnerFactory(
      DispatcherLeaderProcessFactoryFactory dispatcherLeaderProcessFactoryFactory) {
    this.dispatcherLeaderProcessFactoryFactory = dispatcherLeaderProcessFactoryFactory;
  }

  @Override
  public DispatcherRunner createDispatcherRunner(
      LeaderElectionService leaderElectionService,
      FatalErrorHandler fatalErrorHandler,
      Executor ioExecutor,
      RpcService rpcService)
      throws Exception {
    DispatcherLeaderProcessFactory dispatcherLeaderProcessFactory =
        dispatcherLeaderProcessFactoryFactory.createFactory(
            ioExecutor, rpcService, fatalErrorHandler);

    return DefaultDispatcherRunner.create(
        leaderElectionService, fatalErrorHandler, dispatcherLeaderProcessFactory);
  }
}

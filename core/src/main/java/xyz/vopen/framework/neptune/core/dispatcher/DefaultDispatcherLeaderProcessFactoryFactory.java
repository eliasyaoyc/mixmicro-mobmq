package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.annoations.Internal;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherLeaderProcessFactory;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherLeaderProcessFactoryFactory;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import java.util.concurrent.Executor;

/**
 * {@link DefaultDispatcherLeaderProcessFactoryFactory} Factory for {@link
 * DispatcherLeaderProcessFactoryFactory} designed to be used when executing an application.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
@Internal
public class DefaultDispatcherLeaderProcessFactoryFactory
    implements DispatcherLeaderProcessFactoryFactory {
  private final Configuration configuration;
  private final DispatcherFactory dispatcherFactory;

  public DefaultDispatcherLeaderProcessFactoryFactory(
      Configuration configuration, DispatcherFactory dispatcherFactory) {
    this.configuration = configuration;
    this.dispatcherFactory = dispatcherFactory;
  }

  @Override
  public DispatcherLeaderProcessFactory createFactory(
      Executor ioExecutor, RpcService rpcService, FatalErrorHandler fatalErrorHandler) {
    return null;
  }

  public static DefaultDispatcherLeaderProcessFactoryFactory create(
      final Configuration configuration, final DispatcherFactory dispatcherFactory) {
    return new DefaultDispatcherLeaderProcessFactoryFactory(configuration, dispatcherFactory);
  }
}

package xyz.vopen.framework.neptune.core.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

/**
 * {@link DefaultDispatcherComponentFactory} Abstract class which implements the creation of the
 * {@link DispatcherComponent} components.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DefaultDispatcherComponentFactory implements DispatcherComponentFactory {
  private static final Logger logger =
      LoggerFactory.getLogger(DefaultDispatcherComponentFactory.class);

  private final @Nonnull DispatcherRunnerFactory dispatcherRunnerFactory;

  public DefaultDispatcherComponentFactory(
      @Nonnull DispatcherRunnerFactory dispatcherRunnerFactory) {
    this.dispatcherRunnerFactory = dispatcherRunnerFactory;
  }

  @Override
  public DispatcherComponent create(
      Configuration configuration,
      Executor ioExecutor,
      RpcService rpcService,
      FatalErrorHandler fatalErrorHandler)
      throws Exception {
    return null;
  }

  public void run() {

  }
}

package xyz.vopen.framework.neptune.core.jobmanager;

import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.rpc.RpcService;

/**
 * {@link DefaultJobManagerRunnerFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public enum DefaultJobManagerRunnerFactory implements JobManagerRunnerFactory {
  INSTANCE;

  @Override
  public JobManagerRunner createJobManagerRunner(
      Configuration configuration, RpcService rpcService, FatalErrorHandler fatalErrorHandler)
      throws Exception {
    return null;
  }
}

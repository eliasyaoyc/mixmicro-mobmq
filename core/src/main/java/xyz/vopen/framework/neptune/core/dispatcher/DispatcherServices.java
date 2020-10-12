package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.jobmanager.JobManagerRunnerFactory;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;

import javax.annotation.Nonnull;

/**
 * {@link DispatcherServices}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DispatcherServices {

  private final @Nonnull Configuration configuration;
  private final @Nonnull FatalErrorHandler fatalErrorHandler;
  private final @Nonnull JobManagerRunnerFactory jobManagerRunnerFactory;

  public DispatcherServices(
      @Nonnull Configuration configuration,
      @Nonnull FatalErrorHandler fatalErrorHandler,
      @Nonnull JobManagerRunnerFactory jobManagerRunnerFactory) {
    this.configuration = configuration;
    this.fatalErrorHandler = fatalErrorHandler;
    this.jobManagerRunnerFactory = jobManagerRunnerFactory;
  }

  public static DispatcherServices from(
      @Nonnull Configuration configuration,
      @Nonnull FatalErrorHandler fatalErrorHandler,
      @Nonnull DefaultJobManagerRunnerFactory jobManagerRunnerFactory) {
    return new DispatcherServices(configuration, fatalErrorHandler, jobManagerRunnerFactory);
  }

  public @Nonnull Configuration getConfiguration() {
    return configuration;
  }

  public @Nonnull FatalErrorHandler getFatalErrorHandler() {
    return fatalErrorHandler;
  }

  public @Nonnull JobManagerRunnerFactory getJobManagerRunnerFactory() {
    return jobManagerRunnerFactory;
  }
}

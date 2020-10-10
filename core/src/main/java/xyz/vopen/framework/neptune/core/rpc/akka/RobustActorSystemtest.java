package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.ActorSystemImpl;
import akka.actor.BootstrapSetup;
import akka.actor.Props;
import akka.actor.setup.ActorSystemSetup;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.Option;
import scala.concurrent.ExecutionContext;
import xyz.vopen.framework.neptune.common.utils.ExecutorThreadFactory;

import java.util.Optional;

/**
 * {@link RobustActorSystemtest} {@link akka.actor.ActorSystemImpl} which has a configurable {@link
 * java.lang.Thread.UncaughtExceptionHandler}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class RobustActorSystemtest extends ActorSystemImpl {
  private Option<ExecutionContext> defaultExecutionContext;

  public RobustActorSystemtest(
          String name,
          Config applicationConfig,
          ClassLoader classLoader,
          Option<ExecutionContext> defaultExecutionContext,
          Option<Props> guardianProps,
          ActorSystemSetup setup, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    super(name, applicationConfig, classLoader, defaultExecutionContext, guardianProps, setup);
  }

  @Override
  public Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
    Thread.UncaughtExceptionHandler ex = super.uncaughtExceptionHandler();
    return defaultExecutionContext.getOrElse(() -> ex);
  }

  public static RobustActorSystemtest create(String name, Config applicationConfig) {
    return apply(name, ActorSystemSetup.create(BootstrapSetup.apply(applicationConfig)));
  }

  public static RobustActorSystemtest create(
      String name,
      Config applicationConfig,
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    return apply(
        name,
        ActorSystemSetup.create(BootstrapSetup.apply(applicationConfig)),
        uncaughtExceptionHandler);
  }

  public static RobustActorSystemtest apply(String name, ActorSystemSetup setup) {
    return internalApply(name, setup, ExecutorThreadFactory.FatalExitExceptionHandler.INSTANCE);
  }

  public static RobustActorSystemtest apply(
      String name,
      ActorSystemSetup setup,
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    return internalApply(name, setup, uncaughtExceptionHandler);
  }

  public static RobustActorSystemtest internalApply(
      String name,
      ActorSystemSetup setup,
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    Optional<BootstrapSetup> bootstrapSettings = setup.get(BootstrapSetup.class);
    ClassLoader cl = bootstrapSettings.get().classLoader().getOrElse(() -> findClassLoader());
    Config appConfig = bootstrapSettings.get().config().getOrElse(() -> ConfigFactory.load(cl));
    Option<ExecutionContext> defaultEC = bootstrapSettings.get().defaultExecutionContext();

    return new RobustActorSystemtest(name, appConfig, cl, defaultEC, null, setup, uncaughtExceptionHandler);
  }
}

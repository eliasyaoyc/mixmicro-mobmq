package xyz.vopen.framework.neptune.core.jobmanager;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.core.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.core.configuration.ConfigOption;
import xyz.vopen.framework.neptune.core.configuration.ConfigOptions;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.rpc.akka.AkkaUtils;
import xyz.vopen.framework.neptune.core.taskmanager.TaskManager;

import java.util.Optional;

/**
 * {@link BootstrapTools} Tools for starting {@link JobManager} and {@link TaskManager} processor,
 * including the Actor Systems used to run the {@link JobManager} and {@link TaskManager} actors.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class BootstrapTools {
  private static final Logger logger = LoggerFactory.getLogger(BootstrapTools.class);

  private static final ConfigOption<Boolean> USE_LOCAL_DEFAULT_TMP_DIRS =
      ConfigOptions.key("internal.io.tmpdirs.use-local-default").defaultValue(false);

  public static ActorSystem startLocalActorSystem(
      Configuration configuration,
      String actorSystemName,
      Logger logger,
      ActorSystemExecutorConfiguration actorSystemExecutorConfiguration,
      Config customConfig) {
    return null;
  }

  public static ActorSystem startRemoteActorSystem(
      Configuration configuration,
      String actorSystemName,
      String externalAddress,
      String externalPortRange,
      String bindAddress,
      Optional<String> bindAddress1,
      Logger logger,
      ActorSystemExecutorConfiguration actorSystemExecutorConfiguration,
      Config customConfig) {
    return null;
  }

  /** Configuration interface for {@link akka.actor.ActorSystem} underlying executor. */
  public interface ActorSystemExecutorConfiguration {

    /**
     * Create the executor {@link Config} for the respective executpr.
     *
     * @return
     */
    Config getAkkaConfig();
  }

  /** Configuration for a fork join executor. */
  public static class ForkJoinExecutorConfiguration implements ActorSystemExecutorConfiguration {
    private final double parallelismFactor;
    private final int minParallelism;
    private final int maxParallelism;

    public ForkJoinExecutorConfiguration(
        double parallelismFactor, int minParallelism, int maxParallelism) {
      this.parallelismFactor = parallelismFactor;
      this.minParallelism = minParallelism;
      this.maxParallelism = maxParallelism;
    }

    public double getParallelismFactor() {
      return parallelismFactor;
    }

    public int getMinParallelism() {
      return minParallelism;
    }

    public int getMaxParallelism() {
      return maxParallelism;
    }

    @Override
    public Config getAkkaConfig() {
      return AkkaUtils.getForkJoinExecutorConfig(this);
    }

    public static ForkJoinExecutorConfiguration fromConfiguration(
        final Configuration configuration) {
      final double parallelismFactor =
          configuration.getDouble(AkkaOptions.FORK_JOIN_EXECUTOR_PARALLELISM_FACTOR);
      final int minParallelism =
          configuration.getInteger(AkkaOptions.FORK_JOIN_EXECUTOR_PARALLELISM_MIN);
      final int maxParallelism =
          configuration.getInteger(AkkaOptions.FORK_JOIN_EXECUTOR_PARALLELISM_MAX);

      return new ForkJoinExecutorConfiguration(parallelismFactor, minParallelism, maxParallelism);
    }
  }

  /** Configuration for a fixed thread pool executor. */
  public static class FixedThreadPoolExecutorConfiguration
      implements ActorSystemExecutorConfiguration {

    private final int minNumThreads;

    private final int maxNumThreads;

    private final int threadPriority;

    public FixedThreadPoolExecutorConfiguration(
        int minNumThreads, int maxNumThreads, int threadPriority) {
      if (threadPriority < Thread.MIN_PRIORITY || threadPriority > Thread.MAX_PRIORITY) {
        throw new IllegalArgumentException(
            String.format(
                "The thread priority must be within (%s, %s) but it was %s.",
                Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, threadPriority));
      }

      this.minNumThreads = minNumThreads;
      this.maxNumThreads = maxNumThreads;
      this.threadPriority = threadPriority;
    }

    public int getMinNumThreads() {
      return minNumThreads;
    }

    public int getMaxNumThreads() {
      return maxNumThreads;
    }

    public int getThreadPriority() {
      return threadPriority;
    }

    @Override
    public Config getAkkaConfig() {
      return AkkaUtils.getThreadPoolExecutorConfig(this);
    }
  }
}

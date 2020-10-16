package xyz.vopen.framework.neptune.rpc;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import io.netty.channel.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.common.configuration.ConfigOption;
import xyz.vopen.framework.neptune.common.configuration.ConfigOptions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.tuple.Tuple2;
import xyz.vopen.framework.neptune.common.utils.NetUtils;
import xyz.vopen.framework.neptune.rpc.akka.AkkaUtils;

import java.io.IOException;
import java.net.BindException;
import java.util.Iterator;
import java.util.Optional;

/**
 * {@link BootstrapTools} Tools for starting JobManager and TaskManager processor, including the
 * Actor Systems used to run the JobManager and TaskManager actors.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class BootstrapTools {
  private static final Logger logger = LoggerFactory.getLogger(BootstrapTools.class);

  private static final ConfigOption<Boolean> USE_LOCAL_DEFAULT_TMP_DIRS =
      ConfigOptions.key("internal.io.tmpdirs.use-local-default").defaultValue(false);

  /**
   * Starts a local Actor System.
   *
   * @param configuration The Neptune configuration.
   * @param actorSystemName Name of the started ActorSystem.
   * @param logger The logger to output log information.
   * @param actorSystemExecutorConfiguration Configuration for the ActorSystem's underlying
   *     executor.
   * @param customConfig Custom Akka config to be combined with the config derived from Flink
   *     configuration.
   * @return The ActorSystem which has been started.
   * @throws Exception
   */
  public static ActorSystem startLocalActorSystem(
      Configuration configuration,
      String actorSystemName,
      Logger logger,
      ActorSystemExecutorConfiguration actorSystemExecutorConfiguration,
      Config customConfig)
      throws Exception {
    logger.info("Trying to start local actor system");

    try {
      Config akkaConfig =
          AkkaUtils.getAkkaConfig(
              configuration,
              (Tuple2<String, Integer>) null,
              null,
              actorSystemExecutorConfiguration.getAkkaConfig());

      if (customConfig != null) {
        akkaConfig = customConfig.withFallback(akkaConfig);
      }

      return startActorSystem(akkaConfig, actorSystemName, logger);
    } catch (Throwable t) {
      throw new Exception("Could not create actor system", t);
    }
  }

  /**
   * Starts a remote ActorSystem at given address and specific port range.
   *
   * @param configuration The Neptune configuration
   * @param actorSystemName Name of the started {@link ActorSystem}
   * @param externalAddress The external address to access the ActorSystem.
   * @param externalPortRange The choosing range of the external port to access the ActorSystem.
   * @param bindAddress The local address to bind to.
   * @param bindPort The local port to bind to. If not present, then the external port will be used.
   * @param logger The logger to output log information.
   * @param actorSystemExecutorConfiguration configuration for the ActorSystem's underlying executor
   * @param customConfig Custom Akka config to be combined with the config derived from Flink
   *     configuration.
   * @return The ActorSystem which has been started
   * @throws Exception Thrown when actor system cannot be started in specified port range
   */
  public static ActorSystem startRemoteActorSystem(
      Configuration configuration,
      String actorSystemName,
      String externalAddress,
      String externalPortRange,
      String bindAddress,
      Optional<Integer> bindPort,
      Logger logger,
      ActorSystemExecutorConfiguration actorSystemExecutorConfiguration,
      Config customConfig) throws Exception{

    // parse port range definition and create port iterator
    Iterator<Integer> portsIterator;
    try {
      portsIterator = NetUtils.getPortRangeFromString(externalPortRange);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid port range definition: " + externalPortRange);
    }

    while (portsIterator.hasNext()) {
      final int externalPort = portsIterator.next();

      try {
        return startRemoteActorSystem(
                configuration,
                actorSystemName,
                externalAddress,
                externalPort,
                bindAddress,
                bindPort.orElse(externalPort),
                logger,
                actorSystemExecutorConfiguration,
                customConfig);
      }
      catch (Exception e) {
        // we can continue to try if this contains a netty channel exception
        Throwable cause = e.getCause();
        if (!(cause instanceof ChannelException ||
                cause instanceof BindException)) {
          throw e;
        } // else fall through the loop and try the next port
      }
    }

    // if we come here, we have exhausted the port range
    throw new BindException("Could not start actor system on any port in port range "
            + externalPortRange);
  }

  /**
   * Starts a remote Actor System at given address and specific port.
   * @param configuration The Flink configuration.
   * @param actorSystemName Name of the started {@link ActorSystem}
   * @param externalAddress The external address to access the ActorSystem.
   * @param externalPort The external port to access the ActorSystem.
   * @param bindAddress The local address to bind to.
   * @param bindPort The local port to bind to.
   * @param logger the logger to output log information.
   * @param actorSystemExecutorConfiguration configuration for the ActorSystem's underlying executor
   * @param customConfig Custom Akka config to be combined with the config derived from Flink configuration.
   * @return The ActorSystem which has been started.
   * @throws Exception
   */
  private static ActorSystem startRemoteActorSystem(
          Configuration configuration,
          String actorSystemName,
          String externalAddress,
          int externalPort,
          String bindAddress,
          int bindPort,
          Logger logger,
          ActorSystemExecutorConfiguration actorSystemExecutorConfiguration,
          Config customConfig) throws Exception {

    String externalHostPortUrl = NetUtils.unresolvedHostAndPortToNormalizedString(externalAddress, externalPort);
    String bindHostPortUrl = NetUtils.unresolvedHostAndPortToNormalizedString(bindAddress, bindPort);
    logger.info("Trying to start actor system, external address {}, bind address {}.", externalHostPortUrl, bindHostPortUrl);

    try {
      Config akkaConfig = AkkaUtils.getAkkaConfig(
              configuration,
              Tuple2.of(externalAddress,externalPort),
              Tuple2.of(bindAddress,bindPort),
              actorSystemExecutorConfiguration.getAkkaConfig());

      if (customConfig != null) {
        akkaConfig = customConfig.withFallback(akkaConfig);
      }

      return startActorSystem(akkaConfig, actorSystemName, logger);
    }
    catch (Throwable t) {
      if (t instanceof ChannelException) {
        Throwable cause = t.getCause();
        if (cause != null && t.getCause() instanceof BindException) {
          throw new IOException("Unable to create ActorSystem at address " + bindHostPortUrl +
                  " : " + cause.getMessage(), t);
        }
      }
      throw new Exception("Could not create actor system", t);
    }
  }

  /**
   * Starts an Actor System with given Akka config.
   *
   * @param akkaConfig Config of the started ActorSystem.
   * @param actorSystemName Name of the started ActorSystem.
   * @param logger The logger to output log information.
   * @return The ActorSystem which has been started.
   */
  private static ActorSystem startActorSystem(
      Config akkaConfig, String actorSystemName, Logger logger) {
    logger.debug("Using akka configuration\n {}", akkaConfig);
    ActorSystem actorSystem = AkkaUtils.createActorSystem(actorSystemName, akkaConfig);
    logger.info("Actor system started at {}", AkkaUtils.getAddress(actorSystem));
    return actorSystem;
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

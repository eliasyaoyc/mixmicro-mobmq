package xyz.vopen.framework.neptune.rpc.akka;

import akka.actor.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.common.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.common.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.exceptions.IllegalConfigurationException;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.tuple.Tuple2;
import xyz.vopen.framework.neptune.common.utils.NetUtils;
import xyz.vopen.framework.neptune.common.utils.TimeUtil;
import xyz.vopen.framework.neptune.rpc.BootstrapTools;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * {@link AkkaUtils} This class contains utility functions for akka. It contains method to start an
 * actor system with a given akka configuration. Furthermore, the akka configuration used for
 * starting the different actor systems resides in this class.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version {project.version} - 2020/10/6
 */
public class AkkaUtils {
  private static final Logger logger = LoggerFactory.getLogger(AkkaUtils.class);

  private static final String NEPTUNE_ACTOR_SYSTEM_NAME = "neptune";

  public static String getNeptuneActorSystemName() {
    return NEPTUNE_ACTOR_SYSTEM_NAME;
  }

  /**
   * Creates a local actor system without remoting.
   *
   * @param configuration instance containing the user provided configuration values.
   * @return The created actor system.
   */
  public static ActorSystem createLocalActorSystem(Configuration configuration) {
    Config akkaConfig = getAkkaConfig(configuration, null);
    return createActorSystem(akkaConfig);
  }

  /**
   * Creates an actor system bound to the given hostname and port.
   *
   * @param configuration instance containing the user provided configuration values.
   * @param hostname of the network interface to bind to.
   * @param port of to bind to.
   * @return created actor system.
   */
  public static ActorSystem createActorSystem(
      Configuration configuration, String hostname, Integer port) {
    return createActorSystem(configuration, Tuple2.of(hostname, port));
  }

  /**
   * Creates an actor system. If a listening address is specified, then the actor system will listen
   * on that address for message from a remote actor system. If not, then a local actor system will
   * be instantiated.
   *
   * @param configuration instance containing the user provided configuration values.
   * @param listeningAddress aa tuple containing a bindAddress and port to bind to. If the parameter
   *     is null,then a local actor system will be created.
   * @return created actor system.
   */
  public static ActorSystem createActorSystem(
      Configuration configuration, Tuple2<String, Integer> listeningAddress) {
    Config akkaConfig = getAkkaConfig(configuration, listeningAddress);
    return createActorSystem(akkaConfig);
  }

  /**
   * Creates an actor system with the given akka config.
   *
   * @param akkaConfig configuration for the actor system.
   * @return created actor system.
   */
  public static ActorSystem createActorSystem(Config akkaConfig) {
    return createActorSystem(NEPTUNE_ACTOR_SYSTEM_NAME, akkaConfig);
  }

  /**
   * Creates an actor system with the given akka config.
   *
   * @param actorSystemName of the actor system.
   * @param akkaConfig configuration for the actor system.
   * @return created actor system.
   */
  public static ActorSystem createActorSystem(String actorSystemName, Config akkaConfig) {
    return RobustActorSystem.create(actorSystemName, akkaConfig);
  }

  /**
   * Creates an actor system with the default config and listening on a random port of the
   * localhost.
   *
   * @return default actor system listening on a random port of the localhost.
   */
  public static ActorSystem createDefaultActorSystem() {
    return createActorSystem(getDefaultAkkaConfig());
  }

  /**
   * Returns a remote Akka config for the given configuration values.
   *
   * @param configuration containing the user provided configuration values
   * @param hostname to bind against. If null, then the loopback interface is used
   * @param port to bind against
   * @param executorConfig containing the user specified config of executor
   * @return A remote Akka config
   */
  public static Config getAkkaConfig(
      Configuration configuration, String hostname, Integer port, Config executorConfig) {
    return getAkkaConfig(configuration, Tuple2.of(hostname, port), null, executorConfig);
  }

  /**
   * Returns a remote Akka config for the given configuration values.
   *
   * @param configuration containing the user provided configuration values
   * @param hostname to bind against. If null, then the loopback interface is used
   * @param port to bind against
   * @return A remote Akka config
   */
  public static Config getAkkaConfig(Configuration configuration, String hostname, Integer port) {
    return getAkkaConfig(configuration, Tuple2.of(hostname, port));
  }

  /**
   * Return a local Akka config for the given configuration values.
   *
   * @param configuration containing the user provided configuration values
   * @return A local Akka config
   */
  public static Config getAkkaConfig(Configuration configuration) {
    return getAkkaConfig(configuration, null);
  }

  /**
   * Creates an akka config with the provided configuration values. If the listening address is
   * specified, then the actor system will listen on the respective address.
   *
   * @param configuration instance containing the user provided configuration values.
   * @param externalAddress tuple of bindAddress and port to be reachable at. If null is given, then
   *     an Akka config for local actor system wil be returned.
   * @return Akka config.
   */
  public static Config getAkkaConfig(
      Configuration configuration, Tuple2<String, Integer> externalAddress) {
    return getAkkaConfig(
        configuration,
        externalAddress,
        null,
        getForkJoinExecutorConfig(
            BootstrapTools.ForkJoinExecutorConfiguration.fromConfiguration(configuration)));
  }

  /**
   * Creates an akka config with the provided configuration values. If the listening address is
   * specified, then the actor system will listen on the respective address.
   *
   * @param configuration instance containing the user provided configuration values
   * @param externalAddress optional tuple of external address and port to be reachable at. If None
   *     is given, then an Akka config for local actor system will be returned
   * @param bindAddress optional tuple of bind address and port to be used locally. If None is
   *     given, wildcard IP address and the external port wil be used. Take effects only if
   *     externalAddress is not None.
   * @param executorConfig config defining the used executor by the default dispatcher
   * @return Akka config
   */
  public static Config getAkkaConfig(
      Configuration configuration,
      Tuple2<String, Integer> externalAddress,
      Tuple2<String, Integer> bindAddress,
      Config executorConfig) {
    Config defaultConfig = getBasicAkkaConfig(configuration).withFallback(executorConfig);
    if (externalAddress != null) {
      Config remoteConfig;
      if (bindAddress != null) {
        remoteConfig =
            getRemoteAkkaConfig(
                configuration,
                bindAddress.f0,
                bindAddress.f1,
                externalAddress.f0,
                externalAddress.f1);
      } else {
        remoteConfig =
            getRemoteAkkaConfig(
                configuration,
                NetUtils.getWildcardIPAddress(),
                externalAddress.f1,
                externalAddress.f0,
                externalAddress.f1);
      }
      return remoteConfig.withFallback(defaultConfig);
    }
    return defaultConfig;
  }

  /**
   * Creates the default akka configuration which listens on a random port on the local machine. All
   * configuration values are set to default values.
   *
   * @return Neptune Akka default config
   */
  public static Config getDefaultAkkaConfig() {
    return getAkkaConfig(new Configuration<>(), Tuple2.of("", 0));
  }

  /**
   * Gets the basic Akka config which is shared by remote and local actor system.
   *
   * @param configuration instance which contains the user specified values for the configuration.
   * @return Neptune's basic Akka config.
   */
  private static Config getBasicAkkaConfig(Configuration configuration) {
    int akkaThroughput = configuration.getInteger(AkkaOptions.DISPATCHER_THROUGHPUT);
    boolean lifecycleEvents = configuration.getBoolean(AkkaOptions.LOG_LIFECYCLE_EVENTS);

    String jvmExitOnFatalError =
        configuration.getBoolean(AkkaOptions.JVM_EXIT_ON_FATAL_ERROR) ? "on" : "off";
    String logLifecycleEvents = lifecycleEvents ? "on" : "off";
    String supervisorStrategy = EscalatingSupervisorStrategy.class.getCanonicalName();
    String logLevel = getLogLevel();

    String configString =
        "akka {\n"
            + " daemonic = off\n"
            + "\n"
            + " loggers = [\"akka.event.slf4j.Slf4jLogger\"]\n"
            + " logging-filter = \"akka.event.slf4j.Slf4jLoggingFilter\"\n"
            + " log-config-on-start = off\n"
            + "\n"
            + " jvm-exit-on-fatal-error = "
            + jvmExitOnFatalError
            + "\n"
            + "\n"
            + " serialize-messages = off\n"
            + "\n"
            + " loglevel = "
            + logLevel
            + "\n"
            + " stdout-loglevel = OFF\n"
            + "\n"
            + " log-dead-letters = "
            + logLifecycleEvents
            + "\n"
            + " log-dead-letters-during-shutdown = "
            + logLifecycleEvents
            + "\n"
            + "\n"
            + " actor {\n"
            + "   guardian-supervisor-strategy = "
            + supervisorStrategy
            + "\n"
            + "\n"
            + "   warn-about-java-serializer-usage = off\n"
            + "\n"
            + "   default-dispatcher {\n"
            + "     throughput = "
            + akkaThroughput
            + "\n"
            + "   }\n"
            + "\n"
            + "   supervisor-dispatcher {\n"
            + "     type = Dispatcher\n"
            + "     executor = \"thread-pool-executor\"\n"
            + "     thread-pool-executor {\n"
            + "       core-pool-size-min = 1\n"
            + "       core-pool-size-max = 1\n"
            + "     }\n"
            + "   }\n"
            + " }\n"
            + "}";
    Config config = ConfigFactory.parseString(configString);
    return config;
  }

  public static Config getThreadPoolExecutorConfig(
      BootstrapTools.FixedThreadPoolExecutorConfiguration configuration) {

    int threadPriority = configuration.getThreadPriority();
    int minNumThreads = configuration.getMinNumThreads();
    int maxNumThreads = configuration.getMaxNumThreads();

    String configString =
        "akka {\n"
            + "  actor {\n"
            + "    default-dispatcher {\n"
            + "      type = akka.dispatch.PriorityThreadsDispatcher\n"
            + "      executor = \"thread-pool-executor\"\n"
            + "      thread-priority = "
            + threadPriority
            + "\n"
            + "      thread-pool-executor {\n"
            + "        core-pool-size-min = "
            + minNumThreads
            + "\n"
            + "        core-pool-size-max = "
            + maxNumThreads
            + "\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";
    return ConfigFactory.parseString(configString);
  }

  public static Config getForkJoinExecutorConfig(
      BootstrapTools.ForkJoinExecutorConfiguration configuration) {
    double parallelismFactor = configuration.getParallelismFactor();
    int minParallelism = configuration.getMinParallelism();
    int maxParallelism = configuration.getMaxParallelism();
    String configString =
        "akka {\n"
            + "  actor {\n"
            + "    default-dispatcher {\n"
            + "      executor = \"fork-join-executor\"\n"
            + "      fork-join-executor {\n"
            + "        parallelism-factor = "
            + parallelismFactor
            + "\n"
            + "        parallelism-min = "
            + minParallelism
            + "\n"
            + "        parallelism-max = "
            + maxParallelism
            + "\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";
    return ConfigFactory.parseString(configString);
  }

  @VisibleForTesting
  public static Config testDispatcherConfig() {
    String config =
        "akka {"
            + "  actor {"
            + "    default-dispatcher {"
            + "      fork-join-executor {"
            + "        parallelism-factor = 1.0"
            + "        parallelism-min = 2"
            + "        parallelism-max = 4"
            + "      }"
            + "    }"
            + "  }"
            + "}";
    return ConfigFactory.parseString(config);
  }

  private static void validateHeartbeat(
      String pauseParamName,
      Duration pauseValue,
      String intervalParamName,
      Duration intervalValue) {
    if (pauseValue.compareTo(intervalValue) <= 0) {
      throw new IllegalConfigurationException(
          "%s [%s] must greater than %s [%s]",
          pauseParamName, pauseValue, intervalParamName, intervalValue);
    }
  }

  /**
   * Creates a Akka config for a remote actor system listening on port on the network interface
   * identified by bindAddress.
   *
   * @param configuration instance containing the user provided configuration values.
   * @param bindAddress of the network interface to bind on.
   * @param port to bind to or if 0 then Akka picks a free port automatically.
   * @param externalHostname The host name of expect for Akka messages.
   * @param externalPort The port of expect for Akka messages.
   * @return Neptune Akka configuration for remote actor systems.
   */
  private static Config getRemoteAkkaConfig(
      Configuration configuration,
      String bindAddress,
      Integer port,
      String externalHostname,
      Integer externalPort) {

    String normalizedExternalHostname = NetUtils.unresolvedHostToNormalizedString(externalHostname);

    Duration akkaAskTimeout = getTimeout(configuration);

    String startupTimeout =
        TimeUtil.getStringInMillis(
            TimeUtil.parseDuration(
                configuration.getString(
                    AkkaOptions.STARTUP_TIMEOUT,
                    TimeUtil.getStringInMillis(akkaAskTimeout.multipliedBy(10L)))));

    Duration transportHeartbeatIntervalDuration =
        TimeUtil.parseDuration(configuration.getString(AkkaOptions.TRANSPORT_HEARTBEAT_INTERVAL));

    Duration transportHeartbeatPauseDuration =
        TimeUtil.parseDuration(configuration.getString(AkkaOptions.TRANSPORT_HEARTBEAT_PAUSE));

    validateHeartbeat(
        AkkaOptions.TRANSPORT_HEARTBEAT_PAUSE.key(),
        transportHeartbeatPauseDuration,
        AkkaOptions.TRANSPORT_HEARTBEAT_INTERVAL.key(),
        transportHeartbeatIntervalDuration);

    String transportHeartbeatInterval =
        TimeUtil.getStringInMillis(transportHeartbeatIntervalDuration);

    String transportHeartbeatPause = TimeUtil.getStringInMillis(transportHeartbeatPauseDuration);

    double transportThreshold = configuration.getDouble(AkkaOptions.TRANSPORT_THRESHOLD);

    String akkaTCPTimeout =
        TimeUtil.getStringInMillis(
            TimeUtil.parseDuration(configuration.getString(AkkaOptions.TCP_TIMEOUT)));

    String akkaFrameSize = configuration.getString(AkkaOptions.FRAMESIZE);

    boolean lifecycleEvents = configuration.getBoolean(AkkaOptions.LOG_LIFECYCLE_EVENTS);

    String logLifecycleEvents = lifecycleEvents ? "on" : "off";

    boolean akkaEnableSSLConfig = configuration.getBoolean(AkkaOptions.SSL_ENABLED);

    long retryGateClosedFor = configuration.getLong(AkkaOptions.RETRY_GATE_CLOSED_FOR);

    Integer clientSocketWorkerPoolPoolSizeMin =
        configuration.getInteger(AkkaOptions.CLIENT_SOCKET_WORKER_POOL_SIZE_MIN);

    Integer clientSocketWorkerPoolPoolSizeMax =
        configuration.getInteger(AkkaOptions.CLIENT_SOCKET_WORKER_POOL_SIZE_MAX);

    double clientSocketWorkerPoolPoolSizeFactor =
        configuration.getDouble(AkkaOptions.CLIENT_SOCKET_WORKER_POOL_SIZE_FACTOR);

    Integer serverSocketWorkerPoolPoolSizeMin =
        configuration.getInteger(AkkaOptions.SERVER_SOCKET_WORKER_POOL_SIZE_MIN);

    Integer serverSocketWorkerPoolPoolSizeMax =
        configuration.getInteger(AkkaOptions.SERVER_SOCKET_WORKER_POOL_SIZE_MAX);

    double serverSocketWorkerPoolPoolSizeFactor =
        configuration.getDouble(AkkaOptions.SERVER_SOCKET_WORKER_POOL_SIZE_FACTOR);

    //    String configString =
    //        "akka {"
    //            + "  actor {"
    //            + "    provider = \"akka.remote.RemoteActorRefProvider\""
    //            + "  }"
    //            + ""
    //            + "  remote {"
    //            + "    startup-timeout = "
    //            + startupTimeout
    //            + ""
    //            + "    transport-failure-detector{"
    //            + "      acceptable-heartbeat-pause = "
    //            + transportHeartbeatPause
    //            + "      heartbeat-interval = "
    //            + transportHeartbeatInterval
    //            + "      threshold = "
    //            + transportThreshold
    //            + "    }"
    //            + ""
    //            + "    netty {"
    //            + "      tcp {"
    //            + "        transport-class = \"akka.remote.transport.netty.NettyTransport\""
    //            + "        port = "
    //            + externalPort
    //            + "        bind-port = "
    //            + port
    //            + "        connection-timeout = "
    //            + akkaTCPTimeout
    //            + "        maximum-frame-size = "
    //            + akkaFrameSize
    //            + "        tcp-nodelay = on"
    //            + ""
    //            + "        client-socket-worker-pool {"
    //            + "          pool-size-min = "
    //            + clientSocketWorkerPoolPoolSizeMin
    //            + "          pool-size-max = "
    //            + clientSocketWorkerPoolPoolSizeMax
    //            + "          pool-size-factor = "
    //            + clientSocketWorkerPoolPoolSizeFactor
    //            + "        }"
    //            + ""
    //            + "        server-socket-worker-pool {"
    //            + "          pool-size-min = "
    //            + serverSocketWorkerPoolPoolSizeMin
    //            + "          pool-size-max = "
    //            + serverSocketWorkerPoolPoolSizeMax
    //            + "          pool-size-factor = "
    //            + serverSocketWorkerPoolPoolSizeFactor
    //            + "        }"
    //            + "      }"
    //            + "    }"
    //            + ""
    //            + "    log-remote-lifecycle-events = "
    //            + logLifecycleEvents
    //            + ""
    //            + "    retry-gate-closed-for = {"
    //            + retryGateClosedFor
    //            + "\" ms\"}"
    //            + "  }"
    //            + "}";

    String configString =
        "akka {\n"
            + "  actor {\n"
            + "    provider = \"akka.remote.RemoteActorRefProvider\"\n"
            + "  }\n"
            + "\n"
            + "  remote {\n"
            + "    startup-timeout = "
            + startupTimeout
            + "\n"
            + "\n"
            + "    transport-failure-detector{\n"
            + "      acceptable-heartbeat-pause = "
            + transportHeartbeatPause
            + "\n"
            + "      heartbeat-interval = "
            + transportHeartbeatInterval
            + "\n"
            + "      threshold = "
            + transportThreshold
            + "\n"
            + "    }\n"
            + "\n"
            + "    netty {\n"
            + "      tcp {\n"
            + "        transport-class = \"akka.remote.transport.netty.NettyTransport\"\n"
            + "        port = "
            + externalPort
            + "\n"
            + "        bind-port = "
            + port
            + "\n"
            + "        connection-timeout = "
            + akkaTCPTimeout
            + "\n"
            + "        maximum-frame-size = "
            + akkaFrameSize
            + "\n"
            + "        tcp-nodelay = on\n"
            + "\n"
            + "        client-socket-worker-pool {\n"
            + "          pool-size-min = "
            + clientSocketWorkerPoolPoolSizeMin
            + "\n"
            + "          pool-size-max = "
            + clientSocketWorkerPoolPoolSizeMax
            + "\n"
            + "          pool-size-factor = "
            + clientSocketWorkerPoolPoolSizeFactor
            + "\n"
            + "        }\n"
            + "\n"
            + "        server-socket-worker-pool {\n"
            + "          pool-size-min = "
            + serverSocketWorkerPoolPoolSizeMin
            + "\n"
            + "          pool-size-max = "
            + serverSocketWorkerPoolPoolSizeMax
            + "\n"
            + "          pool-size-factor = "
            + serverSocketWorkerPoolPoolSizeFactor
            + "\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "\n"
            + "    log-remote-lifecycle-events = "
            + logLifecycleEvents
            + "\n"
            + "\n"
            + "    retry-gate-closed-for = "
            + retryGateClosedFor
            + "ms\n"
            + "  }\n"
            + "}\n";

    String effectiveHostname = "";
    if (normalizedExternalHostname != null && !normalizedExternalHostname.isEmpty()) {
      effectiveHostname = normalizedExternalHostname;
    }

    //    String hostnameConfigString =
    //        "akka {"
    //            + "  remote {"
    //            + "    netty {"
    //            + "      tcp {"
    //            + "        hostname = "
    //            + effectiveHostname
    //            + "        bind-hostname = "
    //            + bindAddress
    //            + "      }"
    //            + "    }"
    //            + "  }"
    //            + "}";
    String hostnameConfigString =
        "akka {\n"
            + "  remote {\n"
            + "    netty {\n"
            + "      tcp {\n"
            + "        hostname = \""
            + effectiveHostname
            + "\"\n"
            + "        bind-hostname = \""
            + bindAddress
            + "\"\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";
    String s = configString + hostnameConfigString;
    return ConfigFactory.parseString(configString + hostnameConfigString).resolve();
  }

  public static String getLogLevel() {
    String res = null;
    if (logger.isTraceEnabled()) {
      res = "TRACE";
    } else {
      if (logger.isDebugEnabled()) {
        res = "DEBUG";
      } else {
        if (logger.isInfoEnabled()) {
          res = "INFO";
        } else {
          if (logger.isWarnEnabled()) {
            res = "WARNING";
          } else {
            if (logger.isErrorEnabled()) {
              res = "ERROR";
            } else {
              res = "OFF";
            }
          }
        }
      }
    }
    return res;
  }

  public static Duration getTimeout(Configuration config) {
    return TimeUtil.parseDuration(config.getString(AkkaOptions.ASK_TIMEOUT));
  }

  public static Time getTimeoutAsTime(Configuration config) {
    try {
      Duration duration = getTimeout(config);
      return Time.milliseconds(duration.toMillis());
    } catch (NumberFormatException e) {
      throw new IllegalConfigurationException(formatDurationParsingErrorMessage);
    }
  }

  public static Time getDefaultTimeout() {
    Duration duration = TimeUtil.parseDuration(AkkaOptions.ASK_TIMEOUT.defaultValue());
    return Time.milliseconds(duration.toMillis());
  }

  public static Duration getLookupTimeout(Configuration config) {
    return TimeUtil.parseDuration(config.getString(AkkaOptions.LOOKUP_TIMEOUT));
  }

  /**
   * Returns the address of the given [[ActorSystem]]. The [[Address]] object contains the port and
   * the host under which the actor system is reachable
   *
   * @param system [[ActorSystem]] for which the [[Address]] shall be retrieved
   * @return [[Address]] of the given [[ActorSystem]]
   */
  public static Address getAddress(ActorSystem system) {
    return system.provider().getDefaultAddress();
  }

  /**
   * Returns the given [[ActorRef]]'s path string representation with host and port of the
   * [[ActorSystem]] in which the actor is running.
   *
   * @param system [[ActorSystem]] in which the given [[ActorRef]] is running
   * @param actor [[ActorRef]] of the [[Actor]] for which the URL has to be generated
   * @return String containing the [[ActorSystem]] independent URL of the [[Actor]]
   */
  public static String getAkkaURL(ActorSystem system, ActorRef actor) {
    Address address = getAddress(system);
    return actor.path().toStringWithAddress(address);
  }

  /**
   * Returns the AkkaURL for a given [[ActorSystem]] and a path describing a running [[Actor]] in
   * the actor system.
   *
   * @param system [[ActorSystem]] in which the given [[Actor]] is running
   * @param path Path describing an [[Actor]] for which the URL has to be generated
   * @return String containing the [[ActorSystem]] independent URL of an [[Actor]] specified by
   *     path.
   */
  public static String getAkkaURL(ActorSystem system, String path) {
    Address address = getAddress(system);
    return address.toString() + path;
  }

  /**
   * Extracts the hostname and the port of the remote actor system from the given Akka URL. The
   * result is an [[InetSocketAddress]] instance containing the extracted hostname and port. If the
   * Akka URL does not contain the hostname and port information, e.g. a local Akka URL is provided,
   * then an [[Exception]] is thrown.
   *
   * @param akkaURL The URL to extract the host and port from.
   * @throws java.lang.Exception Thrown, if the given string does not represent a proper url
   * @return The InetSocketAddress with the extracted host and port.
   */
  public static InetSocketAddress getInetSocketAddressFromAkkaURL(String akkaURL) throws Exception {
    // AkkaURLs have the form schema://systemName@host:port/.... if it's a remote Akka URL
    try {
      Address address = getAddressFromAkkaURL(akkaURL);
      if (address == null) {
        throw new MalformedURLException();
      }
      return new InetSocketAddress(address.host().get(), (Integer) address.port().get());
    } catch (MalformedURLException e) {
      throw new Exception("Could not retrieve InetSocketAddress from Akka URL : " + akkaURL);
    }
  }

  /**
   * Extracts the [[Address]] from the given akka URL.
   *
   * @param akkaURL to extract the [[Address]] from
   * @return Extracted [[Address]] from the given akka URL
   */
  public static Address getAddressFromAkkaURL(String akkaURL) throws MalformedURLException {
    return AddressFromURIString.apply(akkaURL);
  }

  private static final String formatDurationParsingErrorMessage =
      "Duration format must be \"val unit\", where 'val' is a number and 'unit' is "
          + "(d|day)|(h|hour)|(min|minute)|s|sec|second)|(ms|milli|millisecond)|"
          + "(µs|micro|microsecond)|(ns|nano|nanosecond)";

  /**
   * Returns the local akka url for the given actor name.
   *
   * @param actorName Actor name identifying the actor
   * @return Local Akka URL for the given actor
   */
  public static String getLocalAkkaURL(String actorName) {
    return "akka://neptune/user/" + actorName;
  }

  /**
   * Terminates the given {@link ActorSystem} and returns its termination future.
   *
   * @param actorSystem to terminate.
   * @return Termination future.
   */
  @VisibleForTesting
  public static CompletableFuture<Void> terminatedActorSystem(ActorSystem actorSystem) {
    return FutureUtil.toJava(actorSystem.terminate()).thenAccept(ignored -> {});
  }
}

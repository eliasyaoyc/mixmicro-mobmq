package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.ActorSystem;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.common.utils.NetUtils;
import xyz.vopen.framework.neptune.core.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.core.configuration.Configuration;
import xyz.vopen.framework.neptune.core.highavailability.HighAvailabilityServicesUtils;
import xyz.vopen.framework.neptune.core.jobmanager.BootstrapTools;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static xyz.vopen.framework.neptune.common.utils.NetUtils.isValidClientPort;

/**
 * {@link AkkaRpcServiceUtils} These RPC utilities contain helper methods around RPC use, such as
 * starting on RPC service, or constructing RPC address
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaRpcServiceUtils {
  private static final Logger logger = LoggerFactory.getLogger(AkkaRpcServiceUtils.class);

  private static final String AKKA_TCP = "akka.tcp";
  private static final String AKKA_SSL_TCP = "akka.ssl.tcp";

  static final String SUPERVISOR_NAME = "rpc";

  private static final String SIMPLE_AKKA_CONFIG_TEMPLATE =
      "akka {remote {netty.tcp {maximum-frame-size = %s}}}";

  private static final String MAXIMUM_FRAME_SIZE_PATH = "akka.remote.netty.tcp.maximum-frame-size";

  private static final AtomicLong nextNameOffset = new AtomicLong(0L);

  /** This class is not meant to be instantiated. */
  private AkkaRpcServiceUtils() {}

  // =====================  RPC instantiation   =====================

  public static AkkaRpcService createRemoteRpcService(
      Configuration configuration,
      @Nullable String externalAddress,
      String externalPortAddress,
      @Nullable String bindAddress,
      Optional<Integer> bindPort)
      throws Exception {
    final AkkaRpcServiceBuilder akkaRpcServiceBuilder =
        AkkaRpcServiceUtils.remoteServiceBuilder(
            configuration, externalAddress, externalPortAddress);

    if (bindAddress != null) {
      akkaRpcServiceBuilder.withBindAddress(bindAddress);
    }

    bindPort.ifPresent(akkaRpcServiceBuilder::withBindPort);

    return akkaRpcServiceBuilder.createAndStart();
  }

  @VisibleForTesting
  public static AkkaRpcServiceBuilder remoteServiceBuilder(
      Configuration configuration, @Nullable String externalAddress, int externalPort) {
    return remoteServiceBuilder(configuration, externalAddress, String.valueOf(externalPort));
  }

  public static AkkaRpcServiceBuilder remoteServiceBuilder(
      Configuration configuration, @Nullable String externalAddress, String externalPortRange) {
    return new AkkaRpcServiceBuilder(configuration, logger, externalAddress, externalPortRange);
  }

  public static AkkaRpcServiceBuilder localServiceBuilder(Configuration configuration) {
    return new AkkaRpcServiceBuilder(configuration, logger);
  }

  // =====================   RPC endpoint addressing  =====================

  /**
   * @param hostname The hostname or address where the target RPC service is listening.
   * @param port The port where the target RPC service is listening.
   * @param endpointName The name of the RPC endpoint.
   * @param addressResolution Whether to try address resolution of the given hostname or not. This
   *     allows to fail fast in case that the hostname cannot be resolved.
   * @param config The configuration from which to deduce further settings.
   * @return The RPC URL of the specified RPC endpoint.
   */
  public static String getRpcUrl(
      String hostname,
      int port,
      String endpointName,
      HighAvailabilityServicesUtils.AddressResolution addressResolution,
      Configuration config)
      throws UnknownHostException {
    Preconditions.checkNotNull(config, "config is null");

    final boolean sslEnabled = config.getBoolean(AkkaOptions.SSL_ENABLED);

    return getRpcUrl(
        hostname,
        port,
        endpointName,
        addressResolution,
        sslEnabled ? AkkaProtocol.SSL_TCP : AkkaProtocol.TCP);
  }

  /**
   * @param hostname The hostname or address where the target RPC service is listening.
   * @param port The port where the target RPC service is listening.
   * @param endpointName The name of the RPC endpoint.
   * @param addressResolution Whether to try address resolution of the given hostname or not. This
   *     allows to fail fast in case that the hostname cannot be resolved.
   * @param akkaProtocol True, if security/encryption is enabled, false otherwise.
   * @return The RPC URL of the specified RPC endpoint.
   */
  public static String getRpcUrl(
      String hostname,
      int port,
      String endpointName,
      HighAvailabilityServicesUtils.AddressResolution addressResolution,
      AkkaProtocol akkaProtocol)
      throws UnknownHostException {
    Preconditions.checkNotNull(hostname, "hostname is null");
    Preconditions.checkNotNull(endpointName, "endpointName is null");
    Preconditions.checkArgument(isValidClientPort(port), "port must be in [1, 65535]");

    if (addressResolution
        == HighAvailabilityServicesUtils.AddressResolution.TRY_ADDRESS_RESOLUTION) {
      // Fail fast if the hostname cannot be resolved
      //noinspection ResultOfMethodCallIgnored
      InetAddress.getByName(hostname);
    }

    final String hostPort = NetUtils.unresolvedHostAndPortToNormalizedString(hostname, port);

    return internalRpcUrl(
        endpointName,
        Optional.of(new AkkaRpcServiceUtils.RemoteAddressInformation(hostPort, akkaProtocol)));
  }

  public static String getLocalRpcUrl(String endpointName) {
    return internalRpcUrl(endpointName, Optional.empty());
  }

  private static final class RemoteAddressInformation {
    private final String hostnameAndPort;
    private final AkkaRpcServiceUtils.AkkaProtocol akkaProtocol;

    private RemoteAddressInformation(
        String hostnameAndPort, AkkaRpcServiceUtils.AkkaProtocol akkaProtocol) {
      this.hostnameAndPort = hostnameAndPort;
      this.akkaProtocol = akkaProtocol;
    }

    private String getHostnameAndPort() {
      return hostnameAndPort;
    }

    private AkkaRpcServiceUtils.AkkaProtocol getAkkaProtocol() {
      return akkaProtocol;
    }
  }

  private static String internalRpcUrl(
      String endpointName,
      Optional<AkkaRpcServiceUtils.RemoteAddressInformation> remoteAddressInformation) {
    final String protocolPrefix =
        remoteAddressInformation
            .map(rai -> akkaProtocolToString(rai.getAkkaProtocol()))
            .orElse("akka");
    final Optional<String> optionalHostnameAndPort =
        remoteAddressInformation.map(
            AkkaRpcServiceUtils.RemoteAddressInformation::getHostnameAndPort);

    final StringBuilder url = new StringBuilder(String.format("%s://neptune", protocolPrefix));
    optionalHostnameAndPort.ifPresent(hostPort -> url.append("@").append(hostPort));

    url.append("/user/").append(SUPERVISOR_NAME).append("/").append(endpointName);

    // protocolPrefix://neptune[@hostname:port]/user/rpc/endpointName
    return url.toString();
  }

  private static String akkaProtocolToString(AkkaRpcServiceUtils.AkkaProtocol akkaProtocol) {
    return akkaProtocol == AkkaRpcServiceUtils.AkkaProtocol.SSL_TCP ? AKKA_SSL_TCP : AKKA_TCP;
  }

  /** Whether to use TCP or encrypted TCP for Akka. */
  public enum AkkaProtocol {
    TCP,
    SSL_TCP
  }

  /**
   * Creates a random name of the form prefix_X, where X is an increasing number.
   *
   * @param prefix Prefix string to prepend to the monotonically increasing name offset number
   * @return A random name of the form prefix_X where X is an increasing number
   */
  public static String createRandomName(String prefix) {
    Preconditions.checkNotNull(prefix, "Prefix must not be null.");

    long nameOffset;

    // obtain the next name offset by incrementing it atomically
    do {
      nameOffset = nextNameOffset.get();
    } while (!nextNameOffset.compareAndSet(nameOffset, nameOffset + 1L));

    return prefix + '_' + nameOffset;
  }

  /**
   * Creates a wildcard name symmetric to {@link #createRandomName(String)}.
   *
   * @param prefix prefix of the wildcard name
   * @return wildcard name starting with the prefix
   */
  public static String createWildcardName(String prefix) {
    return prefix + "_*";
  }

  // =====================  RPC service configuration  =====================
  public static long extractMaximumFrameSize(Configuration configuration) {
    String maxFrameSizeStr = configuration.getString(AkkaOptions.FRAMESIZE);
    String akkaConfigStr = String.format(SIMPLE_AKKA_CONFIG_TEMPLATE, maxFrameSizeStr);
    Config akkaConfig = ConfigFactory.parseString(akkaConfigStr);
    return akkaConfig.getBytes(MAXIMUM_FRAME_SIZE_PATH);
  }

  // =====================  RPC service builder  =====================
  /** Builder for {@link AkkaRpcService}. */
  public static class AkkaRpcServiceBuilder {
    private final Configuration configuration;
    private final Logger logger;
    private final @Nullable String externalAddress;
    private final @Nullable String externalPortRange;
    private String actorSystemName = AkkaUtils.getNeptuneActorSystemName();
    private @Nullable BootstrapTools.ActorSystemExecutorConfiguration
        actorSystemExecutorConfiguration = null;
    private @Nullable Config customConfig = null;
    private String bindAddress = NetUtils.getWildcardIPAddress();
    private @Nullable Integer bindPort = null;

    /** Builder for creating a remote RPC service. */
    private AkkaRpcServiceBuilder(
        final Configuration configuration,
        final Logger logger,
        final @Nullable String externalAddress,
        final String externalPortRange) {
      this.configuration = Preconditions.checkNotNull(configuration);
      this.logger = Preconditions.checkNotNull(logger);
      this.externalAddress =
          externalAddress == null
              ? InetAddress.getLoopbackAddress().getHostAddress()
              : externalAddress;
      this.externalPortRange = Preconditions.checkNotNull(externalPortRange);
    }

    /** Builder for creating a local RPC service. */
    private AkkaRpcServiceBuilder(final Configuration configuration, final Logger logger) {
      this.configuration = Preconditions.checkNotNull(configuration);
      this.logger = logger;
      this.externalAddress = null;
      this.externalPortRange = null;
    }

    public AkkaRpcServiceUtils.AkkaRpcServiceBuilder withActorSystemName(
        final String actorSystemName) {
      this.actorSystemName = Preconditions.checkNotNull(actorSystemName);
      return this;
    }

    public AkkaRpcServiceUtils.AkkaRpcServiceBuilder withActorSystemExecutorConfiguration(
        final BootstrapTools.ActorSystemExecutorConfiguration actorSystemExecutorConfiguration) {
      this.actorSystemExecutorConfiguration = actorSystemExecutorConfiguration;
      return this;
    }

    public AkkaRpcServiceUtils.AkkaRpcServiceBuilder withCustomConfig(final Config customConfig) {
      this.customConfig = customConfig;
      return this;
    }

    public AkkaRpcServiceUtils.AkkaRpcServiceBuilder withBindAddress(final String bindAddress) {
      this.bindAddress = Preconditions.checkNotNull(bindAddress);
      return this;
    }

    public AkkaRpcServiceUtils.AkkaRpcServiceBuilder withBindPort(int bindPort) {
      Preconditions.checkArgument(
          NetUtils.isValidHostPort(bindPort), "Invalid port number: " + bindPort);
      this.bindPort = bindPort;
      return this;
    }

    public AkkaRpcService createAndStart() throws Exception {
      if (actorSystemExecutorConfiguration == null) {
        actorSystemExecutorConfiguration =
            BootstrapTools.ForkJoinExecutorConfiguration.fromConfiguration(configuration);
      }

      final ActorSystem actorSystem;

      if (externalAddress == null) {
        // create local actor system.
        actorSystem =
            BootstrapTools.startLocalActorSystem(
                configuration,
                actorSystemName,
                logger,
                actorSystemExecutorConfiguration,
                customConfig);
      } else {
        // create remote actor system.
        actorSystem =
            BootstrapTools.startRemoteActorSystem(
                configuration,
                actorSystemName,
                externalAddress,
                externalPortRange,
                bindAddress,
                Optional.ofNullable(bindAddress),
                logger,
                actorSystemExecutorConfiguration,
                customConfig);
      }
      return new AkkaRpcService(
          actorSystem, AkkaRpcServiceConfiguration.fromConfiguration(configuration));
    }
  }
}

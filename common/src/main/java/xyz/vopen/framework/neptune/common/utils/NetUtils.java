package xyz.vopen.framework.neptune.common.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;
import xyz.vopen.framework.neptune.common.annoations.Internal;
import xyz.vopen.framework.neptune.common.exceptions.IllegalConfigurationException;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/** Utility for various network related tasks (such as finding free ports). */
@Internal
public class NetUtils {

  private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

  /** The wildcard address to listen on all interfaces (either 0.0.0.0 or ::). */
  private static final String WILDCARD_ADDRESS =
      new InetSocketAddress(0).getAddress().getHostAddress();

  private static volatile String HOST_ADDRESS;
  private static final String LOCALHOST_VALUE = "127.0.0.1";
  private static volatile InetAddress LOCAL_ADDRESS = null;
  private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
  private static final String ANYHOST_VALUE = "0.0.0.0";

  /**
   * Returns the local ip.
   *
   * @return the local ip.
   */
  public static String getLocalHost() {
    if (HOST_ADDRESS != null) {
      return HOST_ADDRESS;
    }
    InetAddress address = getLocalAddress();
    if (address != null) {
      return HOST_ADDRESS = address.getHostAddress();
    }
    return LOCALHOST_VALUE;
  }

  /**
   * Find first valid IP from local network card.
   *
   * @return first valid local IP.
   */
  public static InetAddress getLocalAddress() {
    if (LOCAL_ADDRESS != null) {
      return LOCAL_ADDRESS;
    }
    return LOCAL_ADDRESS = getLocalAddress0();
  }

  private static InetAddress getLocalAddress0() {
    InetAddress localAddress = null;

    try {
      NetworkInterface networkInterface = findNetworkInterface();
    } catch (Throwable e) {
      logger.warn("[NetUtils] getLocalAddress0 failed.", e);
    }
    try {
      localAddress = InetAddress.getLocalHost();
      Optional<InetAddress> addressOp = toValidAddress(localAddress);
      if (addressOp.isPresent()) {
        return addressOp.get();
      }
    } catch (Throwable e) {
      logger.warn("[NetUtils] getLocalAddress0 failed.", e);
    }
    return localAddress;
  }

  /**
   * Get the suitable {@link NetworkInterface}
   *
   * @return If no {@link NetworkInterface} is available , return <code>null</code>
   * @since 2.7.6
   */
  public static NetworkInterface findNetworkInterface() {

    List<NetworkInterface> validNetworkInterfaces = emptyList();
    try {
      validNetworkInterfaces = getValidNetworkInterfaces();
    } catch (Throwable e) {
      logger.warn("[Net] findNetworkInterface failed", e);
    }

    NetworkInterface result = null;

    // Try to find the preferred one
    for (NetworkInterface networkInterface : validNetworkInterfaces) {
      if (isPreferredNetworkInterface(networkInterface)) {
        result = networkInterface;
        logger.info("[Net] use preferred network interface: {}", networkInterface.getDisplayName());
        break;
      }
    }

    if (result == null) { // If not found, try to get the first one
      for (NetworkInterface networkInterface : validNetworkInterfaces) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
          if (addressOp.isPresent()) {
            try {
              if (addressOp.get().isReachable(100)) {
                result = networkInterface;
                break;
              }
            } catch (IOException e) {
              // ignore
            }
          }
        }
      }
    }

    if (result == null) {
      result = first(validNetworkInterfaces);
    }

    return result;
  }

  /**
   * Take the first element from the specified collection
   *
   * @param values the collection object
   * @param <T>    the type of element of collection
   * @return if found, return the first one, or <code>null</code>
   * @since 2.7.6
   */
  public static <T> T first(Collection<T> values) {
    if (values == null || values.isEmpty()) {
      return null;
    }
    if (values instanceof List) {
      List<T> list = (List<T>) values;
      return list.get(0);
    } else {
      return values.iterator().next();
    }
  }

  /**
   * Get the valid {@link NetworkInterface network interfaces}
   *
   * @return non-null
   * @throws SocketException SocketException if an I/O error occurs.
   * @since 2.7.6
   */
  private static List<NetworkInterface> getValidNetworkInterfaces() throws SocketException {
    List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
      NetworkInterface networkInterface = interfaces.nextElement();
      if (ignoreNetworkInterface(networkInterface)) { // ignore
        continue;
      }
      // 根据用户 -D 参数忽略网卡
      if (ignoreInterfaceByConfig(networkInterface.getDisplayName())) {
        continue;
      }
      validNetworkInterfaces.add(networkInterface);
    }
    return validNetworkInterfaces;
  }

  /**
   * @param networkInterface {@link NetworkInterface}
   * @return if the specified {@link NetworkInterface} should be ignored, return <code>true</code>
   * @throws SocketException SocketException if an I/O error occurs.
   * @since 2.7.6
   */
  private static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
    return networkInterface == null
            || networkInterface.isLoopback()
            || networkInterface.isVirtual()
            || !networkInterface.isUp();
  }

  /**
   * Is preferred {@link NetworkInterface} or not
   *
   * @param networkInterface {@link NetworkInterface}
   * @return if the name of the specified {@link NetworkInterface} matches
   * the property value from {@code neptune.network.interface.preferred}, return <code>true</code>,
   * or <code>false</code>
   */
  public static boolean isPreferredNetworkInterface(NetworkInterface networkInterface) {
    String preferredNetworkInterface = System.getProperty("neptune.network.interface.preferred");
    return Objects.equals(networkInterface.getDisplayName(), preferredNetworkInterface);
  }

  static boolean ignoreInterfaceByConfig(String interfaceName) {
    String regex = System.getProperty("neptune.network.interface.ignored");
    if (StringUtils.isBlank(regex)) {
      return false;
    }
    if (interfaceName.matches(regex)) {
      logger.info("[Net] ignore network interface: {} by regex({})", interfaceName, regex);
      return true;
    }
    return false;
  }

  private static Optional<InetAddress> toValidAddress(InetAddress address) {
    if (address instanceof Inet6Address) {
      Inet6Address v6Address = (Inet6Address) address;
      if (isPreferIPV6Address()) {
        return Optional.ofNullable(normalizeV6Address(v6Address));
      }
    }
    if (isValidV4Address(address)) {
      return Optional.of(address);
    }
    return Optional.empty();
  }

  /**
   * Check if an ipv6 address
   *
   * @return true if it is reachable
   */
  static boolean isPreferIPV6Address() {
    return Boolean.getBoolean("java.net.preferIPv6Addresses");
  }

  /**
   * normalize the ipv6 Address, convert scope name to scope id. e.g. convert
   * fe80:0:0:0:894:aeec:f37d:23e1%en0 to fe80:0:0:0:894:aeec:f37d:23e1%5
   *
   * <p>The %5 after ipv6 address is called scope id. see java doc of {@link Inet6Address} for more
   * details.
   *
   * @param address the input address
   * @return the normalized address, with scope id converted to int
   */
  static InetAddress normalizeV6Address(Inet6Address address) {
    String addr = address.getHostAddress();
    int i = addr.lastIndexOf('%');
    if (i > 0) {
      try {
        return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
      } catch (UnknownHostException e) {
        // ignore
        logger.debug("Unknown IPV6 address: ", e);
      }
    }
    return address;
  }

  static boolean isValidV4Address(InetAddress address) {
    if (address == null || address.isLoopbackAddress()) {
      return false;
    }

    String name = address.getHostAddress();
    return (name != null
        && IP_PATTERN.matcher(name).matches()
        && !ANYHOST_VALUE.equals(name)
        && !LOCALHOST_VALUE.equals(name));
  }

  /**
   * Turn a fully qualified domain name (fqdn) into a hostname. If the fqdn has multiple subparts
   * (separated by a period '.'), it will take the first part. Otherwise it takes the entire fqdn.
   *
   * @param fqdn The fully qualified domain name.
   * @return The hostname.
   */
  public static String getHostnameFromFQDN(String fqdn) {
    if (fqdn == null) {
      throw new IllegalArgumentException("fqdn is null");
    }
    int dotPos = fqdn.indexOf('.');
    if (dotPos == -1) {
      return fqdn;
    } else {
      return fqdn.substring(0, dotPos);
    }
  }

  /**
   * Converts a string of the form "host:port" into an {@link URL}.
   *
   * @param hostPort The "host:port" string.
   * @return The converted URL.
   */
  public static URL getCorrectHostnamePort(String hostPort) {
    return validateHostPortString(hostPort);
  }

  /**
   * Converts a string of the form "host:port" into an {@link InetSocketAddress}.
   *
   * @param hostPort The "host:port" string.
   * @return The converted InetSocketAddress.
   */
  public static InetSocketAddress parseHostPortAddress(String hostPort) {
    URL url = validateHostPortString(hostPort);
    return new InetSocketAddress(url.getHost(), url.getPort());
  }

  /**
   * Validates if the given String represents a hostname:port.
   *
   * <p>Works also for ipv6.
   *
   * <p>See:
   * http://stackoverflow.com/questions/2345063/java-common-way-to-validate-and-convert-hostport-to-inetsocketaddress
   *
   * @return URL object for accessing host and port
   */
  private static URL validateHostPortString(String hostPort) {
    try {
      URL u = new URL("http://" + hostPort);
      if (u.getHost() == null) {
        throw new IllegalArgumentException(
            "The given host:port ('" + hostPort + "') doesn't contain a valid host");
      }
      if (u.getPort() == -1) {
        throw new IllegalArgumentException(
            "The given host:port ('" + hostPort + "') doesn't contain a valid port");
      }
      return u;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("The given host:port ('" + hostPort + "') is invalid", e);
    }
  }

  // ------------------------------------------------------------------------
  //  Lookup of to free ports
  // ------------------------------------------------------------------------

  /**
   * Find a non-occupied port.
   *
   * @return A non-occupied port.
   */
  public static int getAvailablePort() {
    for (int i = 0; i < 50; i++) {
      try (ServerSocket serverSocket = new ServerSocket(0)) {
        int port = serverSocket.getLocalPort();
        if (port != 0) {
          return port;
        }
      } catch (IOException ignored) {
      }
    }

    throw new RuntimeException("Could not find a free permitted port on the machine.");
  }

  // ------------------------------------------------------------------------
  //  Encoding of IP addresses for URLs
  // ------------------------------------------------------------------------

  /**
   * Returns an address in a normalized format for Akka. When an IPv6 address is specified, it
   * normalizes the IPv6 address to avoid complications with the exact URL match policy of Akka.
   *
   * @param host The hostname, IPv4 or IPv6 address
   * @return host which will be normalized if it is an IPv6 address
   */
  public static String unresolvedHostToNormalizedString(String host) {
    // Return loopback interface address if host is null
    // This represents the behavior of {@code InetAddress.getByName } and RFC 3330
    if (host == null) {
      host = InetAddress.getLoopbackAddress().getHostAddress();
    } else {
      host = host.trim().toLowerCase();
      if (host.startsWith("[") && host.endsWith("]")) {
        String address = host.substring(1, host.length() - 1);
        if (IPAddressUtil.isIPv6LiteralAddress(address)) {
          host = address;
        }
      }
    }

    // normalize and valid address
    if (IPAddressUtil.isIPv6LiteralAddress(host)) {
      byte[] ipV6Address = IPAddressUtil.textToNumericFormatV6(host);
      host = getIPv6UrlRepresentation(ipV6Address);
    } else if (!IPAddressUtil.isIPv4LiteralAddress(host)) {
      try {
        // We don't allow these in hostnames
        Preconditions.checkArgument(!host.startsWith("."));
        Preconditions.checkArgument(!host.endsWith("."));
        Preconditions.checkArgument(!host.contains(":"));
      } catch (Exception e) {
        throw new IllegalConfigurationException("The configured hostname is not valid", e);
      }
    }

    return host;
  }

  /**
   * Returns a valid address for Akka. It returns a String of format 'host:port'. When an IPv6
   * address is specified, it normalizes the IPv6 address to avoid complications with the exact URL
   * match policy of Akka.
   *
   * @param host The hostname, IPv4 or IPv6 address
   * @param port The port
   * @return host:port where host will be normalized if it is an IPv6 address
   */
  public static String unresolvedHostAndPortToNormalizedString(String host, int port) {
    Preconditions.checkArgument(isValidHostPort(port), "Port is not within the valid range,");
    return unresolvedHostToNormalizedString(host) + ":" + port;
  }

  /**
   * Encodes an IP address properly as a URL string. This method makes sure that IPv6 addresses have
   * the proper formatting to be included in URLs.
   *
   * @param address The IP address to encode.
   * @return The proper URL string encoded IP address.
   */
  public static String ipAddressToUrlString(InetAddress address) {
    if (address == null) {
      throw new NullPointerException("address is null");
    } else if (address instanceof Inet4Address) {
      return address.getHostAddress();
    } else if (address instanceof Inet6Address) {
      return getIPv6UrlRepresentation((Inet6Address) address);
    } else {
      throw new IllegalArgumentException("Unrecognized type of InetAddress: " + address);
    }
  }

  /**
   * Encodes an IP address and port to be included in URL. in particular, this method makes sure
   * that IPv6 addresses have the proper formatting to be included in URLs.
   *
   * @param address The address to be included in the URL.
   * @param port The port for the URL address.
   * @return The proper URL string encoded IP address and port.
   */
  public static String ipAddressAndPortToUrlString(InetAddress address, int port) {
    return ipAddressToUrlString(address) + ':' + port;
  }

  /**
   * Encodes an IP address and port to be included in URL. in particular, this method makes sure
   * that IPv6 addresses have the proper formatting to be included in URLs.
   *
   * @param address The socket address with the IP address and port.
   * @return The proper URL string encoded IP address and port.
   */
  public static String socketAddressToUrlString(InetSocketAddress address) {
    if (address.isUnresolved()) {
      throw new IllegalArgumentException("Address cannot be resolved: " + address.getHostString());
    }
    return ipAddressAndPortToUrlString(address.getAddress(), address.getPort());
  }

  /**
   * Normalizes and encodes a hostname and port to be included in URL. In particular, this method
   * makes sure that IPv6 address literals have the proper formatting to be included in URLs.
   *
   * @param host The address to be included in the URL.
   * @param port The port for the URL address.
   * @return The proper URL string encoded IP address and port.
   * @throws UnknownHostException Thrown, if the hostname cannot be translated into a URL.
   */
  public static String hostAndPortToUrlString(String host, int port) throws UnknownHostException {
    return ipAddressAndPortToUrlString(InetAddress.getByName(host), port);
  }

  /**
   * Creates a compressed URL style representation of an Inet6Address.
   *
   * <p>This method copies and adopts code from Google's Guava library. We re-implement this here in
   * order to reduce dependency on Guava. The Guava library has frequently caused dependency
   * conflicts in the past.
   */
  private static String getIPv6UrlRepresentation(Inet6Address address) {
    return getIPv6UrlRepresentation(address.getAddress());
  }

  /**
   * Creates a compressed URL style representation of an Inet6Address.
   *
   * <p>This method copies and adopts code from Google's Guava library. We re-implement this here in
   * order to reduce dependency on Guava. The Guava library has frequently caused dependency
   * conflicts in the past.
   */
  private static String getIPv6UrlRepresentation(byte[] addressBytes) {
    // first, convert bytes to 16 bit chunks
    int[] hextets = new int[8];
    for (int i = 0; i < hextets.length; i++) {
      hextets[i] = (addressBytes[2 * i] & 0xFF) << 8 | (addressBytes[2 * i + 1] & 0xFF);
    }

    // now, find the sequence of zeros that should be compressed
    int bestRunStart = -1;
    int bestRunLength = -1;
    int runStart = -1;
    for (int i = 0; i < hextets.length + 1; i++) {
      if (i < hextets.length && hextets[i] == 0) {
        if (runStart < 0) {
          runStart = i;
        }
      } else if (runStart >= 0) {
        int runLength = i - runStart;
        if (runLength > bestRunLength) {
          bestRunStart = runStart;
          bestRunLength = runLength;
        }
        runStart = -1;
      }
    }
    if (bestRunLength >= 2) {
      Arrays.fill(hextets, bestRunStart, bestRunStart + bestRunLength, -1);
    }

    // convert into text form
    StringBuilder buf = new StringBuilder(40);
    buf.append('[');

    boolean lastWasNumber = false;
    for (int i = 0; i < hextets.length; i++) {
      boolean thisIsNumber = hextets[i] >= 0;
      if (thisIsNumber) {
        if (lastWasNumber) {
          buf.append(':');
        }
        buf.append(Integer.toHexString(hextets[i]));
      } else {
        if (i == 0 || lastWasNumber) {
          buf.append("::");
        }
      }
      lastWasNumber = thisIsNumber;
    }
    buf.append(']');
    return buf.toString();
  }

  // ------------------------------------------------------------------------
  //  Port range parsing
  // ------------------------------------------------------------------------

  /**
   * Returns an iterator over available ports defined by the range definition.
   *
   * @param rangeDefinition String describing a single port, a range of ports or multiple ranges.
   * @return Set of ports from the range definition
   * @throws NumberFormatException If an invalid string is passed.
   */
  public static Iterator<Integer> getPortRangeFromString(String rangeDefinition)
      throws NumberFormatException {
    final String[] ranges = rangeDefinition.trim().split(",");

    UnionIterator<Integer> iterators = new UnionIterator<>();

    for (String rawRange : ranges) {
      Iterator<Integer> rangeIterator;
      String range = rawRange.trim();
      int dashIdx = range.indexOf('-');
      if (dashIdx == -1) {
        // only one port in range:
        final int port = Integer.valueOf(range);
        if (!isValidHostPort(port)) {
          throw new IllegalConfigurationException(
              "Invalid port configuration. Port must be between 0"
                  + "and 65535, but was "
                  + port
                  + ".");
        }
        rangeIterator = Collections.singleton(Integer.valueOf(range)).iterator();
      } else {
        // evaluate range
        final int start = Integer.valueOf(range.substring(0, dashIdx));
        if (!isValidHostPort(start)) {
          throw new IllegalConfigurationException(
              "Invalid port configuration. Port must be between 0"
                  + "and 65535, but was "
                  + start
                  + ".");
        }
        final int end = Integer.valueOf(range.substring(dashIdx + 1, range.length()));
        if (!isValidHostPort(end)) {
          throw new IllegalConfigurationException(
              "Invalid port configuration. Port must be between 0"
                  + "and 65535, but was "
                  + end
                  + ".");
        }
        rangeIterator =
            new Iterator<Integer>() {
              int i = start;

              @Override
              public boolean hasNext() {
                return i <= end;
              }

              @Override
              public Integer next() {
                return i++;
              }

              @Override
              public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
              }
            };
      }
      iterators.add(rangeIterator);
    }

    return iterators;
  }

  /**
   * Tries to allocate a socket from the given sets of ports.
   *
   * @param portsIterator A set of ports to choose from.
   * @param factory A factory for creating the SocketServer
   * @return null if no port was available or an allocated socket.
   */
  public static ServerSocket createSocketFromPorts(
      Iterator<Integer> portsIterator, SocketFactory factory) {
    while (portsIterator.hasNext()) {
      int port = portsIterator.next();
      logger.debug("Trying to open socket on port {}", port);
      try {
        return factory.createSocket(port);
      } catch (IOException | IllegalArgumentException e) {
        if (logger.isDebugEnabled()) {
          logger.debug("Unable to allocate socket on port", e);
        } else {
          logger.info("Unable to allocate on port {}, due to error: {}", port, e.getMessage());
        }
      }
    }
    return null;
  }

  /**
   * Returns the wildcard address to listen on all interfaces.
   *
   * @return Either 0.0.0.0 or :: depending on the IP setup.
   */
  public static String getWildcardIPAddress() {
    return WILDCARD_ADDRESS;
  }

  /** A factory for a local socket from port number. */
  @FunctionalInterface
  public interface SocketFactory {
    ServerSocket createSocket(int port) throws IOException;
  }

  /**
   * Check whether the given port is in right range when connecting to somewhere.
   *
   * @param port the port to check
   * @return true if the number in the range 1 to 65535
   */
  public static boolean isValidClientPort(int port) {
    return 1 <= port && port <= 65535;
  }

  /**
   * check whether the given port is in right range when getting port from local system.
   *
   * @param port the port to check
   * @return true if the number in the range 0 to 65535
   */
  public static boolean isValidHostPort(int port) {
    return 0 <= port && port <= 65535;
  }
}

package xyz.vopen.framework.neptune.common.utils;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * {@link IdGenerateUtil} Self-generated id generator
 *
 * <p>length : 64 bit ,from high position to low position
 *
 * <pre>
 * 1bit   符号位
 * 41bits 时间偏移量从2017年4月1日零点到现在的毫秒数
 * 10bits 机器IP二进制最后10位,例如机器的IP为192.168.1.108,二进制表示:11000000 10101000 00000001 01101100,截取最后10位 01 01101100,转为十进制364,设置workerId为364.
 * 12bits 同一个毫秒内的自增量
 * </pre>
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class IdGenerateUtil {
  private static final Logger LOG = LoggerFactory.getLogger(IdGenerateUtil.class);

  public static final long EPOCH;
  private static final long SEQUENCE_BITS = 6L;
  private static final long WORKER_ID_BITS = 10L;
  private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;
  private static final long WORKER_ID_LEFT_SHIT_BITS = SEQUENCE_BITS;
  private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIT_BITS + WORKER_ID_BITS;
  private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;

  private static AbstractClock clock = ClockSupport.clock();
  private static long workerId;
  private static long sequence;
  private static long lastTime;

  static {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2017, Calendar.APRIL, 1);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    EPOCH = calendar.getTimeInMillis();
    initWorkId();
  }

  static void initWorkId() {
    InetAddress address = getLocalAddress();
    byte[] ipAddressByteArray = address.getAddress();
    long workerId =
        (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE)
            + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF));
  }

  private static InetAddress getLocalAddress() {
    try {
      for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
          interfaces.hasMoreElements(); ) {
        NetworkInterface networkInterface = interfaces.nextElement();
        if (networkInterface.isLoopback()
            || networkInterface.isVirtual()
            || !networkInterface.isUp()) {
          continue;
        }
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        if (addresses.hasMoreElements()) {
          return addresses.nextElement();
        }
      }
    } catch (Exception e) {
      LOG.error("getLocalAddress occur error, ip address : <{}>", e.getMessage());
      throw new IllegalStateException(
          "Can't get Localhost InetAddress, please check your network!");
    }
    return null;
  }

  private static void setWorkerId(final Long workerId) {
    Preconditions.checkArgument(workerId >= 0 && workerId < WORKER_ID_MAX_VALUE);
    IdGenerateUtil.workerId = workerId;
  }

  /**
   * Returns the unique id.
   *
   * @return the unique id.
   */
  public static Long generate() {
    long time = clock.millis();
    Preconditions.checkState(
        lastTime <= time,
        "Clock is moving backward, last time is %d milliseconds, current time is %d milliseconds",
        lastTime,
        time);

    if (lastTime == time) {

    } else {
      sequence = 0;
    }
    lastTime = time;

    if (LOG.isDebugEnabled()) {
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
      String f =
          format.format(
              LocalDateTime.ofInstant(Instant.ofEpochMilli(lastTime), ZoneId.systemDefault()));
      LOG.debug("{}-{}-{}", f, workerId, sequence);
    }

    return ((time - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS)
        | (workerId << WORKER_ID_LEFT_SHIT_BITS)
        | sequence;
  }

  // =====================  HELPER CLASS  =====================
  static class ClockSupport {
    public static AbstractClock clock() {
      return new SystemClock();
    }
  }

  static class SystemClock extends AbstractClock {

    @Override
    long millis() {
      return System.currentTimeMillis();
    }
  }
}

abstract class AbstractClock {
  abstract long millis();
}

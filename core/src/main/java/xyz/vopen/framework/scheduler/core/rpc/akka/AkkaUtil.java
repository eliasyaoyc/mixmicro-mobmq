package xyz.vopen.framework.scheduler.core.rpc.akka;

import xyz.vopen.framework.scheduler.common.time.Time;
import xyz.vopen.framework.scheduler.core.configuration.Configuration;
import xyz.vopen.framework.scheduler.core.exception.IllegalConfigurationException;

/**
 * {@link AkkaUtil}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaUtil {
  private static final String formatDurationParsingErrorMessage =
      "Duration format must be \"val unit\", where 'val' is a number and 'unit' is "
          + "(d|day)|(h|hour)|(min|minute)|s|sec|second)|(ms|milli|millisecond)|"
          + "(µs|micro|microsecond)|(ns|nano|nanosecond)";

  public static Time getTimeoutAsTime(Configuration config) {
    try {
      return null;
    } catch (NumberFormatException e) {
      throw new IllegalConfigurationException(formatDurationParsingErrorMessage);
    }
  }
}

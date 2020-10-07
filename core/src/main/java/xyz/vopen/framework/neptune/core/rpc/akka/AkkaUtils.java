package xyz.vopen.framework.neptune.core.rpc.akka;

import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.TimeUtil;
import xyz.vopen.framework.neptune.core.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.core.exceptions.IllegalConfigurationException;
import xyz.vopen.framework.neptune.core.configuration.Configuration;

import java.time.Duration;

/**
 * {@link AkkaUtils}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaUtils {
  private static final String formatDurationParsingErrorMessage =
      "Duration format must be \"val unit\", where 'val' is a number and 'unit' is "
          + "(d|day)|(h|hour)|(min|minute)|s|sec|second)|(ms|milli|millisecond)|"
          + "(µs|micro|microsecond)|(ns|nano|nanosecond)";

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
}

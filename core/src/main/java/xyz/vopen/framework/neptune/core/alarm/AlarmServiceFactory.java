package xyz.vopen.framework.neptune.core.alarm;

import com.google.common.base.Preconditions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.configuration.JobManagerOptions;

import javax.annotation.Nonnull;

/**
 * {@link AlarmServiceFactory} Factory for alarm service.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public enum AlarmServiceFactory {
  INSTANCE;

  public Alarm create(final @Nonnull Configuration configuration) {
    Alarm.AlarmEnum alarmEnum =
        (Alarm.AlarmEnum)
            configuration.getEnum(Alarm.AlarmEnum.class, JobManagerOptions.ALARM_TYPE);

    Preconditions.checkNotNull(alarmEnum, "Alarm type is empty.");

    switch (alarmEnum) {
      case EMAIL:
        return EmailAlarmService.create(configuration);
      case MESSAGE:
        return MessageAlarmService.create(configuration);
      case WOCOM:
        return WecomAlarmService.create(configuration);
      default:
        throw new IllegalArgumentException("Current alarm fashion is not support.");
    }
  }
}

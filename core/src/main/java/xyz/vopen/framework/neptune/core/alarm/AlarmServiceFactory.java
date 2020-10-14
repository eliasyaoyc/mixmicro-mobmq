package xyz.vopen.framework.neptune.core.alarm;

import xyz.vopen.framework.neptune.common.configuration.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link AlarmServiceFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public enum AlarmServiceFactory {
  INSTANCE;

  public Alarm create(
      final @Nonnull Configuration configuration, final @Nullable Alarm.AlarmEnum alarmEnum) {
    switch (alarmEnum) {
      case EMAIL:
        return EmailAlarmService.create(configuration);
      case MESSAGE:
        return MessageAlarmService.create(configuration);
      default:
        throw new IllegalArgumentException("Current alarm fashion is not support.");
    }
  }
}

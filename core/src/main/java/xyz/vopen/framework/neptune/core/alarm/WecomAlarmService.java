package xyz.vopen.framework.neptune.core.alarm;

import xyz.vopen.framework.neptune.common.configuration.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * {@link WecomAlarmService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class WecomAlarmService implements Alarm {

  private final Configuration configuration;

  private WecomAlarmService(final Configuration configuration) {
    this.configuration = configuration;
  }

  public static WecomAlarmService create(final Configuration configuration) {
    return new WecomAlarmService(configuration);
  }

  @Override
  public CompletableFuture<Void> sendAlarm() {
    return null;
  }
}

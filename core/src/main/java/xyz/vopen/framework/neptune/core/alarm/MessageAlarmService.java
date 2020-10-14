package xyz.vopen.framework.neptune.core.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * {@link MessageAlarmService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class MessageAlarmService extends AlarmService {
  private static final Logger logger = LoggerFactory.getLogger(MessageAlarmService.class);

  private final Configuration configuration;

  MessageAlarmService(final Configuration configuration) {
    this.configuration = configuration;
  }

  // =====================  HELPER =====================
  public static MessageAlarmService create(final Configuration configuration) {
    return new MessageAlarmService(configuration);
  }

  @Override
  public CompletableFuture<Void> sendAlarm() {
    return null;
  }
}

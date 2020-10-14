package xyz.vopen.framework.neptune.core.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * {@link EmailAlarmService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class EmailAlarmService extends AlarmService {
  private static final Logger logger = LoggerFactory.getLogger(EmailAlarmService.class);

  private final Configuration configuration;

  EmailAlarmService(final Configuration configuration){
    this.configuration = configuration;
  }

  // =====================  HELPER =====================
  public static EmailAlarmService create(final Configuration configuration){
    return new EmailAlarmService(configuration);
  }

  @Override
  public CompletableFuture<Void> sendAlarm() {
    return null;
  }
}

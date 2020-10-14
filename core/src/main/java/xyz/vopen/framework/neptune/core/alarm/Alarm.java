package xyz.vopen.framework.neptune.core.alarm;

import java.util.concurrent.CompletableFuture;

/**
 * {@link Alarm}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/4
 */
public interface Alarm {

  CompletableFuture<Void> sendAlarm();

  enum AlarmEnum {
    EMAIL,
    MESSAGE;
  }
}

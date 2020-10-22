package xyz.vopen.framework.neptune.core.alarm;

import java.util.concurrent.CompletableFuture;

/**
 * {@link AlarmService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public abstract class AlarmService implements Alarm {

  @Override
  public void start() {}

  @Override
  public CompletableFuture<Void> stop() {
    return null;
  }
}

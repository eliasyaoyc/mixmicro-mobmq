package xyz.vopen.framework.neptune.common.time.timewheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@link Timer}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public interface Timer {
  /**
   * Schedule task.
   *
   * @param task
   * @param delay
   * @param unit
   * @return
   */
  TimerFuture schedule(TimerTask task, long delay, TimeUnit unit);

  /**
   * Stop all task.
   *
   * @return
   */
  Set<TimerTask> stop();
}

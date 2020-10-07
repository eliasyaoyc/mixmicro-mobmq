package xyz.vopen.framework.neptune.common.time.timewheel;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.utils.NumberUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link HashedWheelTimer}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class HashedWheelTimer implements Timer {
  private static final Logger logger = LoggerFactory.getLogger(HashedWheelTimer.class);

  private final long tickDuration;
  private final HashedWheelBucket[] wheel;
  private final int mask;

  private final Indicator indicator;
  private final long startTime;

  private final Queue<HashedWheelTimerFuture> waitingTasks = Queues.newLinkedBlockingQueue();
  private final Queue<HashedWheelTimerFuture> canceledTasks = Queues.newLinkedBlockingDeque();

  private final ExecutorService taskProcessPool;

  public HashedWheelTimer(long tickDuration, int ticksPerWheel) {
    this(tickDuration, ticksPerWheel, 0);
  }

  public HashedWheelTimer(long tickDuration, int ticksPerWheel, int processThreadNum) {
    this.tickDuration = tickDuration;

    int ticksNum = NumberUtil.formatSize(ticksPerWheel);
    this.wheel = new HashedWheelBucket[ticksNum];
    for (int i = 0; i < ticksNum; i++) {
      this.wheel[i] = new HashedWheelBucket();
    }
    this.mask = wheel.length - 1;

    if (processThreadNum <= 0) {
      this.taskProcessPool = null;
    } else {
      this.taskProcessPool =
          new ThreadPoolExecutor(
              Runtime.getRuntime().availableProcessors(),
              Runtime.getRuntime().availableProcessors() * 2,
              60,
              TimeUnit.SECONDS,
              Queues.newLinkedBlockingQueue(16),
              new ThreadFactoryBuilder().setNameFormat("HashedWheelTimer-Executor-%d").build(),
              new ThreadPoolExecutor.CallerRunsPolicy());
    }

    this.startTime = System.currentTimeMillis();
    this.indicator = new Indicator();
    new Thread(this.indicator, "HashedWheelTimer-Indicator").start();
  }

  @Override
  public TimerFuture schedule(TimerTask task, long delay, TimeUnit unit) {
    return null;
  }

  @Override
  public Set<TimerTask> stop() {
    return null;
  }

  private final class HashedWheelBucket extends LinkedList<HashedWheelTimerFuture> {}

  private class Indicator implements Runnable {

    @Override
    public void run() {}
  }

  private final class HashedWheelTimerFuture implements TimerFuture {
    @Override
    public TimerTask getTask() {
      return null;
    }

    @Override
    public boolean cancel() {
      return false;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return false;
    }
  }
}

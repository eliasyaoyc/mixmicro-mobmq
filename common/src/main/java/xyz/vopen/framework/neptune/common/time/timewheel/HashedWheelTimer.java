package xyz.vopen.framework.neptune.common.time.timewheel;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.utils.NumberUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static xyz.vopen.framework.neptune.common.time.timewheel.HashedWheelTimer.TaskStatus.*;

/**
 * {@link HashedWheelTimer} Time wheel like Netty.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class HashedWheelTimer implements Timer {
  private static final Logger LOG = LoggerFactory.getLogger(HashedWheelTimer.class);

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
    long targetTime = System.currentTimeMillis() + unit.toMillis(delay);

    HashedWheelTimerFuture hashedWheelTimerFuture = new HashedWheelTimerFuture(task, targetTime);

    // run expired and overdue tasks directly
    if (delay <= 0) {
      runTask(hashedWheelTimerFuture);
    } else {
      // TODO: 2020/10/22  写入阻塞队列，保证并发安全（性能进一步优化可以考虑 Netty 的 Multi-Producer-Single-Consumer队列）
      waitingTasks.add(hashedWheelTimerFuture);
    }

    return hashedWheelTimerFuture;
  }

  private void runTask(HashedWheelTimerFuture timerFuture) {
    timerFuture.status = RUNNING;
    if (taskProcessPool == null) {
      timerFuture.timerTask.run();
    } else {
      taskProcessPool.submit(timerFuture.timerTask);
    }
  }

  @Override
  public Set<TimerTask> stop() {
    indicator.stop.set(true);
    taskProcessPool.shutdown();
    while (!taskProcessPool.isTerminated()) {
      try {
        Thread.sleep(100);
      } catch (Exception ignore) {

      }
    }
    return indicator.getUnprocessedTasks();
  }

  private final class HashedWheelTimerFuture implements TimerFuture {

    /** Expected execution time */
    private final long targetTime;

    private final TimerTask timerTask;
    /** The time grid to which it belongs,used to quickly delete the task. */
    private HashedWheelBucket bucket;

    private long totalTicks;
    /**
     * 0-initializer
     *
     * <p>1-running
     *
     * <p>2-completed
     *
     * <p>3-canceled
     */
    private TaskStatus status;

    public HashedWheelTimerFuture(TimerTask timerTask, long targetTime) {
      this.targetTime = targetTime;
      this.timerTask = timerTask;
      this.status = WAITING;
    }

    @Override
    public TimerTask getTask() {
      return timerTask;
    }

    @Override
    public boolean cancel() {
      if (status == WAITING) {
        status = CANCELED;
        canceledTasks.add(this);
        return true;
      }
      return false;
    }

    @Override
    public boolean isCancelled() {
      return status == CANCELED;
    }

    @Override
    public boolean isDone() {
      return status == COMPLETED;
    }
  }

  private final class HashedWheelBucket extends LinkedList<HashedWheelTimerFuture> {
    public void expireTimerTasks(long currentTask) {
      removeIf(
          timerFuture -> {
            if (timerFuture.status != WAITING) {
              LOG.warn("[HashedWheelTimer] impossible");
              return true;
            }

            if (timerFuture.totalTicks <= currentTask) {
              if (timerFuture.totalTicks < currentTask) {
                LOG.warn("[HashedWheelTimer] timerFuture.totalTicks < currentTick");
              }
              try {
                runTask(timerFuture);
              } catch (Exception ignore) {
              } finally {
                timerFuture.status = COMPLETED;
              }
              return true;
            }
            return false;
          });
    }
  }

  /** Analog pointer rotation */
  private class Indicator implements Runnable {
    private long tick = 0;
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void run() {
      while (!stop.get()) {
        pushTaskToBucket();

        processCanceledTasks();

        tickTack();

        int currentIndex = (int) (tick & mask);
        HashedWheelBucket bucket = wheel[currentIndex];
        bucket.expireTimerTasks(tick);

        tick++;
      }

      latch.countDown();
    }

    /** Push tasks in the queue into the time wheel */
    private void pushTaskToBucket() {
      while (true) {
        HashedWheelTimerFuture timerTask = waitingTasks.poll();
        if (timerTask == null) {
          return;
        }

        long offset = timerTask.targetTime - startTime;
        timerTask.totalTicks = offset / tickDuration;
        int index = (int) (timerTask.totalTicks & mask);
        HashedWheelBucket bucket = wheel[index];

        timerTask.bucket = bucket;

        if (timerTask.status == WAITING) {
          bucket.add(timerTask);
        }
      }
    }

    private void processCanceledTasks() {
      while (true) {
        HashedWheelTimerFuture canceledTask = canceledTasks.poll();

        if (canceledTask == null) {
          return;
        }

        if (canceledTask.bucket != null) {
          canceledTask.bucket.remove(canceledTask);
        }
      }
    }

    private void tickTack() {
      long nextTime = startTime + (tick + 1) * tickDuration;
      long sleepTime = nextTime - System.currentTimeMillis();

      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (Exception ignore) {

        }
      }
    }

    public Set<TimerTask> getUnprocessedTasks() {
      try {
        latch.await();
      } catch (Exception ignore) {
      }
      HashSet<TimerTask> tasks = Sets.newHashSet();

      Consumer<HashedWheelTimerFuture> consumer =
          timerFuture -> {
            if (timerFuture.status == WAITING) {
              tasks.add(timerFuture.timerTask);
            }
          };

      waitingTasks.forEach(consumer);
      for (HashedWheelBucket bucket : wheel) {
        bucket.forEach(consumer);
      }
      return tasks;
    }
  }

  enum TaskStatus {
    WAITING(0),
    RUNNING(1),
    COMPLETED(2),
    CANCELED(3);

    int status;

    TaskStatus(int status) {
      this.status = status;
    }
  }
}

package xyz.vopen.framework.scheduler.common.time;

import xyz.vopen.framework.scheduler.common.annoations.Internal;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * {@link Deadline} This class stores a deadline, as obtain via {@link #now} or from {@link
 * #plus(java.time.Duration)}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
@Internal
public class Deadline {

  /** The deadline, relative to {@link System#nanoTime()}. */
  private final long timeNanos;

  private Deadline(long deadline) {
    this.timeNanos = deadline;
  }

  public Deadline plus(Duration other) {
    return new Deadline(Math.addExact(timeNanos, other.toNanos()));
  }

  /**
   * Returns the time left between the deadline and now, The result is negative if the deadline has
   * passed.
   *
   * @return
   */
  public Duration timeLeft() {
    return Duration.ofNanos(Math.subtractExact(timeNanos, System.nanoTime()));
  }

  /**
   * Return the left between the deadline now, if no time is left, a {@link TimeoutException} will
   * be thrown,
   *
   * @return
   * @throws TimeoutException if no time is left.
   */
  public Duration timeLeftIfAny() throws TimeoutException {
    long nanos = Math.subtractExact(timeNanos, System.nanoTime());
    if (nanos <= 0) {
      throw new TimeoutException();
    }
    return Duration.ofNanos(nanos);
  }

  /**
   * Returns whether there is any time left between the deadline and now.
   *
   * @return
   */
  public boolean hasTimeLeft() {
    return !isOverDue();
  }

  /**
   * Determines whether the deadline is in the past, i.e. whether the time left is negative.
   *
   * @return
   */
  public boolean isOverDue() {
    return timeNanos < System.nanoTime();
  }

  /**
   * Constructs a {@link Deadline} that has now as the deadline. Use this and then extend via {@link
   * #plus(Duration)} to specify a deadline in the future.
   *
   * @return
   */
  public static Deadline now() {
    return new Deadline(System.nanoTime());
  }

  /**
   * Constructs a Deadline that is a given duration after now.
   *
   * @param duration
   * @return
   */
  public static Deadline fromNow(Duration duration) {
    return new Deadline(Math.addExact(System.nanoTime(), duration.toNanos()));
  }
}

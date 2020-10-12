package xyz.vopen.framework.neptune.rpc.message;

import com.google.common.base.Preconditions;

/**
 * {@link RunAsync} Message for asynchronous runnable invocations.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public final class RunAsync {
  private final Runnable runnable;

  /** The delay after which the runnable should be called. */
  private final long atTimeNanos;

  public RunAsync(Runnable runnable, long atTimeNanos) {
    Preconditions.checkArgument(atTimeNanos > 0);
    this.runnable = runnable;
    this.atTimeNanos = atTimeNanos;
  }

  public Runnable getRunnable() {
    return runnable;
  }

  public long getTimeNanos() {
    return atTimeNanos;
  }
}

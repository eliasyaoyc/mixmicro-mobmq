package xyz.vopen.framework.neptune.common.concurrent;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@link ComponentMainThreadExecutor}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public interface ComponentMainThreadExecutor extends ScheduledExecutor {
  /** Returns true if the method was called in the thread of this executor. */
  void assertRunningInMainThread();

  /** Dummy implementation of ComponentMainThreadExecutor. */
  final class DummyComponentMainThreadExecutor implements ComponentMainThreadExecutor {

    /** Customized message for the exception that is thrown on method invocation. */
    private final String exceptionMessageOnInvocation;

    public DummyComponentMainThreadExecutor(String exceptionMessageOnInvocation) {
      this.exceptionMessageOnInvocation = exceptionMessageOnInvocation;
    }

    @Override
    public void assertRunningInMainThread() {
      throw createException();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      throw createException();
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      throw createException();
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(
        Runnable command, long initialDelay, long period, TimeUnit unit) {
      throw createException();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(
        Runnable command, long initialDelay, long delay, TimeUnit unit) {
      throw createException();
    }

    @Override
    public void execute(@Nonnull Runnable command) {
      throw createException();
    }

    private UnsupportedOperationException createException() {
      return new UnsupportedOperationException(exceptionMessageOnInvocation);
    }
  }
}

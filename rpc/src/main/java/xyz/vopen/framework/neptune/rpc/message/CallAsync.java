package xyz.vopen.framework.neptune.rpc.message;

import com.google.common.base.Preconditions;

import java.util.concurrent.Callable;

/**
 * {@link CallAsync} Message for asynchronous callable invocation.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public final class CallAsync {

  private final Callable<?> callable;

  public CallAsync(Callable<?> callable) {
    this.callable = Preconditions.checkNotNull(callable);
  }

  public Callable<?> getCallable() {
    return this.callable;
  }
}

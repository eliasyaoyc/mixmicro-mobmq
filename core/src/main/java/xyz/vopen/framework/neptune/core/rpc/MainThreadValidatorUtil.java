package xyz.vopen.framework.neptune.core.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * {@link MainThreadValidatorUtil} This utility exists to bridge between the visibility of the
 * {@code currentMainThread} field in the {@link RpcEndpoint}.
 *
 * <p>The {@code currentMainThread} can be hidden from {@code RpcEndpoint} implementations and only
 * be accessed via this utility from other packages.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class MainThreadValidatorUtil {
  private static final Logger logger = LoggerFactory.getLogger(MainThreadValidatorUtil.class);

  private RpcEndpoint rpcEndpoint;

  public MainThreadValidatorUtil(RpcEndpoint rpcEndpoint) {
    this.rpcEndpoint = rpcEndpoint;
  }

  public void enterMainThread() {
    assert (this.rpcEndpoint.currentMainThread.compareAndSet(null, Thread.currentThread()))
        : "The RpcEndpoint has current access from" + this.rpcEndpoint.currentMainThread.get();
  }

  public void exitMainThread() {
    assert (this.rpcEndpoint.currentMainThread.compareAndSet(Thread.currentThread(), null))
        : "The RpcEndpoint has current access from" + this.rpcEndpoint.currentMainThread.get();
  }

  /**
   * Returns true if the current thread is equals to the provided expected thread and logs
   * violations,
   *
   * @param expected the expected main thread.
   * @return true if the current thread is equals to the provided expected thread.
   */
  public static boolean isRunningInExpectedThread(@Nullable Thread expected) {
    Thread actual = Thread.currentThread();
    if (expected != actual) {
      String violationMsg =
          "Violation of main thread constraint detected: expected <"
              + expected
              + "> but running in <"
              + actual
              + ">.";

      logger.warn(violationMsg, new Exception(violationMsg));
      return false;
    }
    return true;
  }
}

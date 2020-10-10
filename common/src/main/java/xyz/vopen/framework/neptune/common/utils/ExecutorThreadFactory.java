package xyz.vopen.framework.neptune.common.utils;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * {@link ExecutorThreadFactory} A thread factory intended for use by critical thread pools.
 * Critical thread pools here mean thread pools that support Flink's core coordination and
 * processing work, and which must not simply cause unnoticed errors.
 *
 * <p>The thread factory can be given an {@link UncaughtExceptionHandler} for the threads. If no
 * handler is explicitly given, the default handler for uncaught exceptions will log the exceptions
 * and kill the process afterwards. That guarantees that critical exceptions are not accidentally
 * lost and leave the system running in an inconsistent state.
 *
 * <p>Threads created by this factory are all called '(pool-name)-thread-n', where
 * <i>(pool-name)</i> is configurable, and <i>n</i> is an incrementing number.
 *
 * <p>All threads created by this factory are daemon threads and have the default (normal) priority.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class ExecutorThreadFactory implements ThreadFactory {
  /** The thread pool name used when no explicit pool name has been specified */
  private static final String DEFAULT_POOL_NAME = "neptune-executor-pool";

  private final AtomicInteger threadNumber = new AtomicInteger(1);

  private final ThreadGroup group;

  private final String namePrefix;

  private final int threadPriority;

  private final @Nullable UncaughtExceptionHandler exceptionHandler;

  /**
   * Creates a new thread factory using the default thread pool name ('flink-executor-pool') and the
   * default uncaught exception handler (log exception and kill process).
   */
  public ExecutorThreadFactory() {
    this(DEFAULT_POOL_NAME);
  }

  /**
   * Creates a new thread factory using the given thread pool name and the default uncaught
   * exception handler (log exception and kill process).
   *
   * @param poolName The pool name, used as the threads' name prefix
   */
  public ExecutorThreadFactory(String poolName) {
    this(poolName, FatalExitExceptionHandler.INSTANCE);
  }

  /**
   * Creates a new thread factory using the given thread pool name and the given uncaught exception
   * handler.
   *
   * @param poolName The pool name, used as the threads' name prefix
   * @param exceptionHandler The uncaught exception handler for the threads
   */
  public ExecutorThreadFactory(String poolName, UncaughtExceptionHandler exceptionHandler) {
    this(poolName, Thread.NORM_PRIORITY, exceptionHandler);
  }

  ExecutorThreadFactory(
      final String poolName,
      final int threadPriority,
      final @Nullable UncaughtExceptionHandler handler) {
    this.namePrefix = Preconditions.checkNotNull(poolName, "poolName") + "-thread-";
    this.threadPriority = threadPriority;
    this.exceptionHandler = handler;

    SecurityManager securityManager = System.getSecurityManager();
    this.group =
        (securityManager != null)
            ? securityManager.getThreadGroup()
            : Thread.currentThread().getThreadGroup();
  }

  @Override
  public Thread newThread(Runnable runnable) {
    Thread t =
        new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement());
    t.setDaemon(true);

    t.setPriority(this.threadPriority);

    if (this.exceptionHandler != null) {
      t.setUncaughtExceptionHandler(this.exceptionHandler);
    }
    return t;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String poolName;
    private int priority = Thread.NORM_PRIORITY;
    private UncaughtExceptionHandler handler = FatalExitExceptionHandler.INSTANCE;

    public Builder withPoolName(final String poolName) {
      this.poolName = poolName;
      return this;
    }

    public Builder withPriority(final int priority) {
      this.priority = priority;
      return this;
    }

    public Builder withExceptionHandler(final UncaughtExceptionHandler handler) {
      this.handler = handler;
      return this;
    }

    public ExecutorThreadFactory build() {
      return new ExecutorThreadFactory(poolName, priority, handler);
    }
  }

  /**
   * Handler for uncaught exceptions that will log the exception and kill the process afterwards.
   *
   * <p>This guarantees that critical exceptions are not accidentally lost and leave the system
   * running in an inconsistent state.
   */
  public static final class FatalExitExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(FatalExitExceptionHandler.class);

    public static final FatalExitExceptionHandler INSTANCE = new FatalExitExceptionHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
      try {
        logger.error(
            "FATAL: Thread '"
                + t.getName()
                + "' produced an uncaught exception. Stopping the process...",
            e);
      } finally {
        System.exit(-17);
      }
    }
  }
}

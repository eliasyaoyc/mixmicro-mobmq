package xyz.vopen.framework.neptune.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@link ExecutorStUtil} Utilities for {@link java.util.concurrent.Executor} shutdown gracefully.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class ExecutorStUtil {
  private static final Logger logger = LoggerFactory.getLogger(ExecutorStUtil.class);

  /**
   * Gracefully shutdown the given {@link ExecutorService}. The call waits the given timeout that
   * all ExecutorServices terminate. if the ExecutorService do not terminate in this time. they will
   * be shut down hard.
   *
   * @param timeout
   * @param unit
   * @param executorServices
   */
  public static void gracefulShutdown(
      long timeout, TimeUnit unit, ExecutorService... executorServices) {
    for (ExecutorService executorService : executorServices) {
      executorService.shutdown();
    }

    boolean wasInterrupted = false;
    final long endTime = unit.toMillis(timeout) + System.currentTimeMillis();
    long timeLeft = unit.toMillis(timeout);
    boolean hasTimeLeft = timeLeft > 0;

    for (ExecutorService executorService : executorServices) {
      if (wasInterrupted || !hasTimeLeft) {
        executorService.shutdownNow();
      } else {
        try {
          if (!executorService.awaitTermination(timeLeft, TimeUnit.MILLISECONDS)) {
            logger.warn("ExecutorService did not terminate in time. Shutting it down now.");
            executorService.shutdownNow();
          }
        } catch (InterruptedException e) {
          logger.warn(
              "Interrupted while shutting down executor services. Shutting all "
                  + "remaining ExecutorServices down now.",
              e);

          executorService.shutdownNow();

          wasInterrupted = true;

          Thread.currentThread().interrupt();
        }

        timeLeft = endTime - System.currentTimeMillis();
        hasTimeLeft = timeLeft > 0L;
      }
    }
  }

  /**
   * Shuts the given {@link ExecutorService} down in a non-blocking fashion, The shut down will be
   * executed by a thread from the common fork-join pool.
   *
   * <p>The executor services will be shut down gracefully for the given timeout period. Afterwards
   * {@link ExecutorService#shutdownNow()} will be called.
   *
   * @param timeout before {@link ExecutorService#shutdownNow()} is called.
   * @param unit time unit of the timeout.
   * @param executorServices to shut down
   * @return Future which is completed once the {@link ExecutorService} are shut down.
   */
  public static CompletableFuture<Void> nonBlockingShutdown(
      long timeout, TimeUnit unit, ExecutorService... executorServices) {
    return CompletableFuture.supplyAsync(
        () -> {
          gracefulShutdown(timeout, unit, executorServices);
          return null;
        });
  }
}

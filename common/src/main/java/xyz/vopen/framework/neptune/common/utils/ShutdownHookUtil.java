package xyz.vopen.framework.neptune.common.utils;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;

/**
 * {@link ShutdownHookUtil} Utility class for dealing with JVM shutdown hooks.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/11
 */
public class ShutdownHookUtil {

  private ShutdownHookUtil() {
    throw new AssertionError();
  }

  /**
   * Adds a shutdown hook to the JVM and returns the Thread, which has been registered.
   *
   * @param service
   * @param serviceName
   * @param logger
   * @return
   */
  public static Thread addShutdownHook(
      final AutoCloseable service, final String serviceName, final Logger logger) {
    Preconditions.checkNotNull(service);
    Preconditions.checkNotNull(logger);

    final Thread shutdownHook =
        new Thread(
            () -> {
              try {
                service.close();
              } catch (Throwable t) {
                logger.error("Error during shutdown of {} via JVM shutdown hook.", serviceName, t);
              }
            },
            serviceName + " shutdown hook");

    return addShutdownHookThread(shutdownHook, serviceName, logger) ? shutdownHook : null;
  }

  /**
   * Adds a shutdown hook to the JVM.
   *
   * @param shutdownHook
   * @param serviceName
   * @param logger
   * @return
   */
  public static boolean addShutdownHookThread(
      final Thread shutdownHook, final String serviceName, final Logger logger) {
    Preconditions.checkNotNull(shutdownHook);
    Preconditions.checkNotNull(logger);

    try {
      Runtime.getRuntime().addShutdownHook(shutdownHook);
      return true;
    } catch (IllegalStateException e) {
    } catch (Throwable t) {
      logger.error("Cannot register shutdown hook that cleanly terminates {}.", serviceName, t);
    }
    return false;
  }

  /**
   * Removes a shutdown hook from the JVM.
   *
   * @param shutdownHook
   * @param serviceName
   * @param logger
   */
  public static void removeShutdownHook(
      final Thread shutdownHook, final String serviceName, final Logger logger) {
    if (shutdownHook == null || shutdownHook == Thread.currentThread()) {
      return;
    }

    Preconditions.checkNotNull(logger);

    try {
      Runtime.getRuntime().removeShutdownHook(shutdownHook);
    } catch (IllegalStateException e) {
      logger.debug(
          "Unable to remove shutdown hook for {}, shutdown already in progress", serviceName, e);
    } catch (Throwable t) {
      logger.warn("Exception while un-registering {}'s shutdown hook.", serviceName);
    }
  }
}

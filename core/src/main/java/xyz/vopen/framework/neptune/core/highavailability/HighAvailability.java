package xyz.vopen.framework.neptune.core.highavailability;

import java.util.concurrent.CompletableFuture;

/**
 * {@link HighAvailability}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public interface HighAvailability {

  void start();

  CompletableFuture<Void> stop();
}

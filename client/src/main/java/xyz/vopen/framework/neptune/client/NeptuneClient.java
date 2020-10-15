package xyz.vopen.framework.neptune.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.client.autoconfigure.NeptuneProperties;
import xyz.vopen.framework.neptune.common.AutoCloseableAsync;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link NeptuneClient}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public class NeptuneClient implements AutoCloseableAsync {
  private static final Logger logger = LoggerFactory.getLogger(NeptuneClient.class);

  private final @Nonnull NeptuneProperties neptuneProperties;
  private final CompletableFuture<Void> terminatedFuture;
  private AtomicBoolean isShutDown = new AtomicBoolean(false);

  public NeptuneClient(final @Nonnull NeptuneProperties neptuneProperties) {
    this.terminatedFuture = new CompletableFuture<>();
    this.neptuneProperties = neptuneProperties;
  }

  public void init() {}

  public void start() {}

  public CompletableFuture<Void> stop() {

    return null;
  }

  public CompletableFuture<Void> getTerminatedFuture() {
    return this.terminatedFuture;
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return shutdownAsync().thenAccept(ignored -> {});
  }

  private CompletableFuture<Void> shutdownAsync() {
    if (isShutDown.compareAndSet(false, true)) {

      logger.info("[NeptuneClient] Stopping...");

      CompletableFuture<Void> shutdownFuture = this.stop();

      shutdownFuture.whenComplete(
          (Void ignored, Throwable throwable) -> {
            if (throwable != null) {
              terminatedFuture.completeExceptionally(throwable);
            } else {
              terminatedFuture.complete(ignored);
            }
          });
    }
    return terminatedFuture;
  }
}

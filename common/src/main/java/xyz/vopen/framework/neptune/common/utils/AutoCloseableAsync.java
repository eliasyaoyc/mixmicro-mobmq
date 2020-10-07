package xyz.vopen.framework.neptune.common.utils;

import xyz.vopen.framework.neptune.common.exception.NeptuneException;

import java.util.concurrent.CompletableFuture;

/**
 * {@link AutoCloseableAsync} Closeable interface which allows to close a resource in a non blocking
 * fashion.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public interface AutoCloseableAsync extends AutoCloseable {

  CompletableFuture<Void> closeAsync();

  default void close() throws Exception {
    try {
      closeAsync().get();
    } catch (Exception e) {
      throw new NeptuneException(
          "Could not close resource.", ExceptionUtil.stripExecutionException(e));
    }
  }
}

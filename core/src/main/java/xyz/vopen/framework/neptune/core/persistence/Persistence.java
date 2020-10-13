package xyz.vopen.framework.neptune.core.persistence;

import java.util.concurrent.CompletableFuture;

/**
 * {@link Persistence}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public interface Persistence {


  void initialize();

  CompletableFuture<Void> persist();

  enum PersistenceEnum {
    MONGO;
  }
}

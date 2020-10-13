package xyz.vopen.framework.neptune.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.core.persistence.support.PersistenceAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DefaultPersistence}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DefaultPersistence implements Persistence {
  private static final Logger logger = LoggerFactory.getLogger(DefaultPersistence.class);

  private final @Nonnull Configuration configuration;
  private final PersistenceAdapter persistenceAdapter;

  private DefaultPersistence(
      final Configuration configuration, final PersistenceAdapter persistenceAdapter) {
    this.configuration = configuration;
    this.persistenceAdapter = persistenceAdapter;

    initialize();
  }

  public static Persistence create(
      Configuration configuration, PersistenceAdapter persistenceAdapter) {
    return new DefaultPersistence(configuration, persistenceAdapter);
  }

  @Override
  public void initialize() {}

  @Override
  public CompletableFuture<Void> persist() {
    return persistenceAdapter.persist();
  }
}

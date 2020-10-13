package xyz.vopen.framework.neptune.core.persistence;

import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.core.persistence.support.DefaultPersistenceAdapter;
import xyz.vopen.framework.neptune.core.persistence.support.MongoPersistenceAdapter;
import xyz.vopen.framework.neptune.core.persistence.support.PersistenceAdapter;

import javax.annotation.Nonnull;

/**
 * {@link PersistenceFactory}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public enum PersistenceFactory {
  INSTANCE;

  public Persistence create(final Configuration configuration) {
    return DefaultPersistence.create(configuration, createAdapter(configuration, null));
  }

  public Persistence create(
          final Configuration configuration,
          final @Nonnull Persistence.PersistenceEnum persistenceEnum) {
    return DefaultPersistence.create(configuration, createAdapter(configuration, persistenceEnum));
  }

  protected PersistenceAdapter createAdapter(
      final Configuration configuration, final Persistence.PersistenceEnum persistenceEnum) {
    switch (persistenceEnum) {
      case MONGO:
        return MongoPersistenceAdapter.create(configuration);

      default:
        return DefaultPersistenceAdapter.create(configuration);
    }
  }
}

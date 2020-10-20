package xyz.vopen.framework.neptune.core.persistence.adapter;

import xyz.vopen.framework.neptune.common.configuration.Configuration;

import javax.annotation.Nonnull;

/**
 * {@link MongoPersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class MongoPersistenceAdapter implements PersistenceAdapter {

  private MongoPersistenceAdapter(Configuration configuration) {

  }

  public static MongoPersistenceAdapter create(final @Nonnull Configuration configuration) {
    return new MongoPersistenceAdapter(configuration);
  }
}

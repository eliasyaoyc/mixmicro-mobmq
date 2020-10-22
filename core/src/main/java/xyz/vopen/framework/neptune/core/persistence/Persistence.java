package xyz.vopen.framework.neptune.core.persistence;

import xyz.vopen.framework.neptune.core.persistence.adapter.PersistenceAdapter;

/**
 * {@link Persistence}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public interface Persistence {

  void initialize();

  PersistenceAdapter getPersistenceAdapter();

  enum PersistenceEnum {
    MONGO;
  }
}

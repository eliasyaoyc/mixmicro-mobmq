package xyz.vopen.framework.neptune.repository.api;

import xyz.vopen.framework.neptune.repository.api.exception.NeptuneRepositoryException;
import xyz.vopen.framework.neptune.repository.api.model.ConfigurationModel;

import java.util.Optional;

/**
 * {@link ConfigurationRepository}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public interface ConfigurationRepository {

  /**
   * Query Scheduler Configuration Properties With <code> configId</code> and <code>configVersion
   * </code>.
   *
   * @return
   * @throws NeptuneRepositoryException
   */
  Optional<ConfigurationModel> find(final String configId, final String configVersion)
      throws NeptuneRepositoryException;

  /**
   * Save Scheduler Configuration.
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  int save(ConfigurationModel configurationModel) throws NeptuneRepositoryException;

  /**
   * Update Scheduler Configuration.
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  Optional<ConfigurationModel> update(ConfigurationModel configurationModel)
      throws NeptuneRepositoryException;

  /**
   * Remove the specifies configuration.
   *
   * @return
   * @throws NeptuneRepositoryException
   */
  int remove() throws NeptuneRepositoryException;
}

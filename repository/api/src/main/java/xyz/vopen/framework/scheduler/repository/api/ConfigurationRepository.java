package xyz.vopen.framework.scheduler.repository.api;

import xyz.vopen.framework.scheduler.repository.api.exception.SchedulerRepositoryException;
import xyz.vopen.framework.scheduler.repository.api.model.ConfigurationModel;

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
   * @throws SchedulerRepositoryException
   */
  Optional<ConfigurationModel> find(final String configId, final String configVersion)
      throws SchedulerRepositoryException;

  /**
   * Save Scheduler Configuration.
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  int save(ConfigurationModel configurationModel) throws SchedulerRepositoryException;

  /**
   * Update Scheduler Configuration.
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  Optional<ConfigurationModel> update(ConfigurationModel configurationModel)
      throws SchedulerRepositoryException;

  /**
   * Remove the specifies configuration.
   *
   * @return
   * @throws SchedulerRepositoryException
   */
  int remove() throws SchedulerRepositoryException;
}

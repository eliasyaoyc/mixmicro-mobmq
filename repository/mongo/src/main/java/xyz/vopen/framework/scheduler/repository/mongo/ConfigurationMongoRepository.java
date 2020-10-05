package xyz.vopen.framework.scheduler.repository.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.scheduler.repository.api.ConfigurationRepository;
import xyz.vopen.framework.scheduler.repository.api.exception.SchedulerRepositoryException;
import xyz.vopen.framework.scheduler.repository.api.model.ConfigurationModel;

import java.util.Optional;

/**
 * {@link ConfigurationMongoRepository}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class ConfigurationMongoRepository implements ConfigurationRepository {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationMongoRepository.class);

  /**
   *
   * @param configId
   * @param configVersion
   * @return
   * @throws SchedulerRepositoryException
   */
  @Override
  public Optional<ConfigurationModel> find(String configId, String configVersion)
      throws SchedulerRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  @Override
  public int save(ConfigurationModel configurationModel) throws SchedulerRepositoryException {
    return 0;
  }

  /**
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  @Override
  public Optional<ConfigurationModel> update(ConfigurationModel configurationModel)
      throws SchedulerRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @return
   * @throws SchedulerRepositoryException
   */
  @Override
  public int remove() throws SchedulerRepositoryException {
    return 0;
  }
}

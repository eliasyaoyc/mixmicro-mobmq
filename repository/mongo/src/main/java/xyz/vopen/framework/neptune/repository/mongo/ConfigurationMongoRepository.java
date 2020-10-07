package xyz.vopen.framework.neptune.repository.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.repository.api.ConfigurationRepository;
import xyz.vopen.framework.neptune.repository.api.exception.NeptuneRepositoryException;
import xyz.vopen.framework.neptune.repository.api.model.ConfigurationModel;

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
   * @throws NeptuneRepositoryException
   */
  @Override
  public Optional<ConfigurationModel> find(String configId, String configVersion)
      throws NeptuneRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  @Override
  public int save(ConfigurationModel configurationModel) throws NeptuneRepositoryException {
    return 0;
  }

  /**
   *
   * @param configurationModel the {@link ConfigurationModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  @Override
  public Optional<ConfigurationModel> update(ConfigurationModel configurationModel)
      throws NeptuneRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @return
   * @throws NeptuneRepositoryException
   */
  @Override
  public int remove() throws NeptuneRepositoryException {
    return 0;
  }
}

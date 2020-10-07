package xyz.vopen.framework.neptune.repository.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.repository.api.TaskRepository;
import xyz.vopen.framework.neptune.repository.api.exception.NeptuneRepositoryException;
import xyz.vopen.framework.neptune.repository.api.model.TaskModel;

import java.util.Optional;

/**
 * {@link TaskMongoRepository}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class TaskMongoRepository implements TaskRepository {
  private static final Logger logger = LoggerFactory.getLogger(TaskMongoRepository.class);

  /**
   *
   * @param taskId
   * @return
   * @throws NeptuneRepositoryException
   */
  public Optional<TaskModel> find(String taskId) throws NeptuneRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  public int save(TaskModel taskModel) throws NeptuneRepositoryException {
    return 0;
  }

  /**
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  public Optional<TaskModel> update(TaskModel taskModel) throws NeptuneRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @return
   * @throws NeptuneRepositoryException
   */
  public int remove() throws NeptuneRepositoryException {
    return 0;
  }
}

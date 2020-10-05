package xyz.vopen.framework.scheduler.repository.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.scheduler.repository.api.TaskRepository;
import xyz.vopen.framework.scheduler.repository.api.exception.SchedulerRepositoryException;
import xyz.vopen.framework.scheduler.repository.api.model.TaskModel;

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
   * @throws SchedulerRepositoryException
   */
  public Optional<TaskModel> find(String taskId) throws SchedulerRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  public int save(TaskModel taskModel) throws SchedulerRepositoryException {
    return 0;
  }

  /**
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  public Optional<TaskModel> update(TaskModel taskModel) throws SchedulerRepositoryException {
    return Optional.empty();
  }

  /**
   *
   * @return
   * @throws SchedulerRepositoryException
   */
  public int remove() throws SchedulerRepositoryException {
    return 0;
  }
}

package xyz.vopen.framework.scheduler.repository.api;

import xyz.vopen.framework.scheduler.repository.api.exception.SchedulerRepositoryException;
import xyz.vopen.framework.scheduler.repository.api.model.ConfigurationModel;
import xyz.vopen.framework.scheduler.repository.api.model.TaskModel;

import java.util.Optional;

/**
 * {@link TaskRepository}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public interface TaskRepository {

  /**
   * Query Task With <code> taskId</code>.
   *
   * @return the {@link TaskModel} instance.
   * @throws SchedulerRepositoryException
   */
  Optional<TaskModel> find(final String taskId) throws SchedulerRepositoryException;

  /**
   * Save Task.
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  int save(TaskModel taskModel) throws SchedulerRepositoryException;

  /**
   * Update Task.
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws SchedulerRepositoryException
   */
  Optional<TaskModel> update(TaskModel taskModel) throws SchedulerRepositoryException;

  /**
   * Remove the specifies task.
   *
   * @return
   * @throws SchedulerRepositoryException
   */
  int remove() throws SchedulerRepositoryException;
}

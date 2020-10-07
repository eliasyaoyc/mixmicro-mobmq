package xyz.vopen.framework.neptune.repository.api;

import xyz.vopen.framework.neptune.repository.api.exception.NeptuneRepositoryException;
import xyz.vopen.framework.neptune.repository.api.model.TaskModel;

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
   * @throws NeptuneRepositoryException
   */
  Optional<TaskModel> find(final String taskId) throws NeptuneRepositoryException;

  /**
   * Save Task.
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  int save(TaskModel taskModel) throws NeptuneRepositoryException;

  /**
   * Update Task.
   *
   * @param taskModel the {@link TaskModel} instance.
   * @return
   * @throws NeptuneRepositoryException
   */
  Optional<TaskModel> update(TaskModel taskModel) throws NeptuneRepositoryException;

  /**
   * Remove the specifies task.
   *
   * @return
   * @throws NeptuneRepositoryException
   */
  int remove() throws NeptuneRepositoryException;
}

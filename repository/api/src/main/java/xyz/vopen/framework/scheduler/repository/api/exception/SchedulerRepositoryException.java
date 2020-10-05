package xyz.vopen.framework.scheduler.repository.api.exception;

import javax.validation.constraints.NotNull;

/**
 * {@link SchedulerRepositoryException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class SchedulerRepositoryException extends RuntimeException {

  public SchedulerRepositoryException() {
    super();
  }

  public SchedulerRepositoryException(@NotNull String message) {
    super(message);
  }

  public SchedulerRepositoryException(@NotNull Throwable cause) {
    super(cause);
  }

  public SchedulerRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
}

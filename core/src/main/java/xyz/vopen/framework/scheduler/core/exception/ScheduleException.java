package xyz.vopen.framework.scheduler.core.exception;

import javax.validation.constraints.NotNull;

/**
 * {@link ScheduleException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/4
 */
public class ScheduleException extends RuntimeException {
  static final long serialVersionUID = -7034897190745766939L;

  public ScheduleException() {
    super();
  }

  public ScheduleException(@NotNull String message) {
    super(message);
  }

  public ScheduleException(@NotNull Throwable cause) {
    super(cause);
  }

  public ScheduleException(String message, Throwable cause) {
    super(message, cause);
  }
}

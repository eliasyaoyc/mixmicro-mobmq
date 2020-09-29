package xyz.vopen.framework.scheduler.common.exception;

import javax.validation.constraints.NotNull;

/**
 * {@link SchedulerException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public class SchedulerException extends RuntimeException {
  static final long serialVersionUID = -7034897190745766939L;

  public SchedulerException() {
    super();
  }

  public SchedulerException(@NotNull String message) {
    super(message);
  }

  public SchedulerException(@NotNull Throwable cause) {
    super(cause);
  }

  public SchedulerException(String message, Throwable cause) {
    super(message, cause);
  }
}

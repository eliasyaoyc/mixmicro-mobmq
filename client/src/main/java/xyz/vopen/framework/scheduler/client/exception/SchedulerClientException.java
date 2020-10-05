package xyz.vopen.framework.scheduler.client.exception;

import javax.validation.constraints.NotNull;

/**
 * {@link SchedulerClientException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class SchedulerClientException extends RuntimeException {
  static final long serialVersionUID = -7034897190745766939L;

  public SchedulerClientException() {
    super();
  }

  public SchedulerClientException(@NotNull String message) {
    super(message);
  }

  public SchedulerClientException(@NotNull Throwable cause) {
    super(cause);
  }

  public SchedulerClientException(String message, Throwable cause) {
    super(message, cause);
  }
}

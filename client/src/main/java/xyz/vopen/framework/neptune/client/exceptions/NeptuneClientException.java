package xyz.vopen.framework.neptune.client.exceptions;

import javax.validation.constraints.NotNull;

/**
 * {@link NeptuneClientException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class NeptuneClientException extends RuntimeException {
  static final long serialVersionUID = -7034897190745766939L;

  public NeptuneClientException() {
    super();
  }

  public NeptuneClientException(@NotNull String message) {
    super(message);
  }

  public NeptuneClientException(@NotNull Throwable cause) {
    super(cause);
  }

  public NeptuneClientException(String message, Throwable cause) {
    super(message, cause);
  }
}

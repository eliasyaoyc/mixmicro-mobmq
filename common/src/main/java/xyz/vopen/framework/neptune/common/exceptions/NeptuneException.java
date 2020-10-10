package xyz.vopen.framework.neptune.common.exceptions;

import javax.validation.constraints.NotNull;

/**
 * {@link NeptuneException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
public class NeptuneException extends Exception {
  static final long serialVersionUID = -7034897190745766939L;

  public NeptuneException() {
    super();
  }

  public NeptuneException(@NotNull String message) {
    super(message);
  }

  public NeptuneException(@NotNull Throwable cause) {
    super(cause);
  }

  public NeptuneException(String message, Throwable cause) {
    super(message, cause);
  }
}

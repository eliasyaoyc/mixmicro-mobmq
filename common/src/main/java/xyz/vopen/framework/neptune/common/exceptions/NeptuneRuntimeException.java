package xyz.vopen.framework.neptune.common.exceptions;

import javax.validation.constraints.NotNull;

/**
 * {@link NeptuneRuntimeException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class NeptuneRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 3324750093394292820L;

  public NeptuneRuntimeException() {
    super();
  }

  public NeptuneRuntimeException(@NotNull String message) {
    super(message);
  }

  public NeptuneRuntimeException(@NotNull Throwable cause) {
    super(cause);
  }

  public NeptuneRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}

package xyz.vopen.framework.neptune.core.exceptions;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.annotation.Nonnull;

/**
 * {@link NeptuneEntrypointException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class NeptuneEntrypointException extends NeptuneException {
  private static final long serialVersionUID = -3643711294083256779L;

  public NeptuneEntrypointException() {
    super();
  }

  public NeptuneEntrypointException(@Nonnull String message) {
    super(message);
  }

  public NeptuneEntrypointException(@Nonnull Throwable cause) {
    super(cause);
  }

  public NeptuneEntrypointException(String message, Throwable cause) {
    super(message, cause);
  }
}

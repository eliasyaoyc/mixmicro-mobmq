package xyz.vopen.framework.neptune.core.exceptions;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.annotation.Nonnull;

/**
 * {@link DispatcherException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DispatcherException extends NeptuneException {
  private static final long serialVersionUID = -7741258570664988350L;

  public DispatcherException() {
    super();
  }

  public DispatcherException(@Nonnull String message) {
    super(message);
  }

  public DispatcherException(@Nonnull Throwable cause) {
    super(cause);
  }

  public DispatcherException(String message, Throwable cause) {
    super(message, cause);
  }
}

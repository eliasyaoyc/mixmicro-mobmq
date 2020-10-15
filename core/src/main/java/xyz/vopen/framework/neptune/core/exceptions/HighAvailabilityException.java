package xyz.vopen.framework.neptune.core.exceptions;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.annotation.Nonnull;

/**
 * {@link HighAvailabilityException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public class HighAvailabilityException extends NeptuneException {
  private static final long serialVersionUID = -6167599874497788045L;

  public HighAvailabilityException() {
    super();
  }

  public HighAvailabilityException(@Nonnull String message) {
    super(message);
  }

  public HighAvailabilityException(@Nonnull Throwable cause) {
    super(cause);
  }

  public HighAvailabilityException(String message, Throwable cause) {
    super(message, cause);
  }
}

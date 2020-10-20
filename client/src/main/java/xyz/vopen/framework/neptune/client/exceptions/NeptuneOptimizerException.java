package xyz.vopen.framework.neptune.client.exceptions;

import javax.annotation.Nonnull;

/**
 * {@link NeptuneOptimizerException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class NeptuneOptimizerException extends NeptuneClientException {
  private static final long serialVersionUID = 789973462302680797L;

  public NeptuneOptimizerException() {
    super();
  }

  public NeptuneOptimizerException(@Nonnull String message) {
    super(message);
  }

  public NeptuneOptimizerException(@Nonnull Throwable cause) {
    super(cause);
  }

  public NeptuneOptimizerException(String message, Throwable cause) {
    super(message, cause);
  }
}

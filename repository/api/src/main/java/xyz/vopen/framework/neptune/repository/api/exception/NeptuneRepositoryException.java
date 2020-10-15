package xyz.vopen.framework.neptune.repository.api.exception;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.validation.constraints.NotNull;

/**
 * {@link NeptuneRepositoryException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class NeptuneRepositoryException extends NeptuneException {

  private static final long serialVersionUID = 2526533273307098812L;

  public NeptuneRepositoryException() {
    super();
  }

  public NeptuneRepositoryException(@NotNull String message) {
    super(message);
  }

  public NeptuneRepositoryException(@NotNull Throwable cause) {
    super(cause);
  }

  public NeptuneRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
}

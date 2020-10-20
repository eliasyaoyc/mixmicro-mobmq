package xyz.vopen.framework.neptune.optimizer;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.validation.constraints.NotNull;

/**
 * {@link NeptuneOptimizerException} Exceptions which indicates {@link Optimizer} exception.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class NeptuneOptimizerException extends NeptuneException {
  private static final long serialVersionUID = 104083340169948202L;

  public NeptuneOptimizerException() {
    super();
  }

  public NeptuneOptimizerException(@NotNull String message) {
    super(message);
  }

  public NeptuneOptimizerException(@NotNull Throwable cause) {
    super(cause);
  }

  public NeptuneOptimizerException(String message, Throwable cause) {
    super(message, cause);
  }
}

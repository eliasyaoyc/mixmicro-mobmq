package xyz.vopen.framework.neptune.core.exceptions.rpc;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneRuntimeException;

import javax.validation.constraints.NotNull;

/**
 * {@link AkkaRpcInvalidStateException}Exception which indicates an invalid state.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class AkkaRpcInvalidStateException extends NeptuneRuntimeException {

  private static final long serialVersionUID = -4735433008215851285L;

  public AkkaRpcInvalidStateException() {
    super();
  }

  public AkkaRpcInvalidStateException(@NotNull String message) {
    super(message);
  }

  public AkkaRpcInvalidStateException(@NotNull Throwable cause) {
    super(cause);
  }

  public AkkaRpcInvalidStateException(String message, Throwable cause) {
    super(message, cause);
  }
}

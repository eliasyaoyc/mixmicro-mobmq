package xyz.vopen.framework.neptune.core.exceptions.rpc;

import xyz.vopen.framework.neptune.common.exception.NeptuneRuntimeException;

import javax.validation.constraints.NotNull;

/**
 * {@link RpcRuntimeException} Basic class for RPC related runtime exception.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class RpcRuntimeException extends NeptuneRuntimeException {
  private static final long serialVersionUID = 2587225540331261458L;

  public RpcRuntimeException() {
    super();
  }

  public RpcRuntimeException(@NotNull String message) {
    super(message);
  }

  public RpcRuntimeException(@NotNull Throwable cause) {
    super(cause);
  }

  public RpcRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}

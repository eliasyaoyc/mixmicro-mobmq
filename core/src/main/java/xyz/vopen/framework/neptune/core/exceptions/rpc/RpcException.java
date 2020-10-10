package xyz.vopen.framework.neptune.core.exceptions.rpc;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.validation.constraints.NotNull;

/**
 * {@link RpcException} Base class for RPC related exceptions.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class RpcException extends NeptuneException {

  private static final long serialVersionUID = 5542826287211726603L;

  public RpcException() {
    super();
  }

  public RpcException(@NotNull String message) {
    super(message);
  }

  public RpcException(@NotNull Throwable cause) {
    super(cause);
  }

  public RpcException(String message, Throwable cause) {
    super(message, cause);
  }
}

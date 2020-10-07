package xyz.vopen.framework.neptune.core.exceptions.rpc;

import javax.validation.constraints.NotNull;

/**
 * {@link RpcConnectionException} Exception class which is thrown if a rpc connection failed.
 * Usually this happens if the remote host cannot be reached.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class RpcConnectionException extends RpcException {
  private static final long serialVersionUID = -6487495074279581843L;

  public RpcConnectionException() {
    super();
  }

  public RpcConnectionException(@NotNull String message) {
    super(message);
  }

  public RpcConnectionException(@NotNull Throwable cause) {
    super(cause);
  }

  public RpcConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}

package xyz.vopen.framework.neptune.core.exceptions.rpc;

import javax.validation.constraints.NotNull;

/**
 * {@link HandshakeException} Exception which signals a handshake failure.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class HandshakeException extends RpcRuntimeException {
  private static final long serialVersionUID = -7943835651923610813L;

  public HandshakeException() {
    super();
  }

  public HandshakeException(@NotNull String message) {
    super(message);
  }

  public HandshakeException(@NotNull Throwable cause) {
    super(cause);
  }

  public HandshakeException(String message, Throwable cause) {
    super(message, cause);
  }
}

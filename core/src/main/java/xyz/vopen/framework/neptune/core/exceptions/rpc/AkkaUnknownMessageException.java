package xyz.vopen.framework.neptune.core.exceptions.rpc;

import javax.validation.constraints.NotNull;

/**
 * {@link AkkaUnknownMessageException} Exception which indicates that the AkkaRpcActor has received
 * an unknown message type.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class AkkaUnknownMessageException extends RpcRuntimeException {

  private static final long serialVersionUID = -2278730860366781082L;

  public AkkaUnknownMessageException() {
    super();
  }

  public AkkaUnknownMessageException(@NotNull String message) {
    super(message);
  }

  public AkkaUnknownMessageException(@NotNull Throwable cause) {
    super(cause);
  }

  public AkkaUnknownMessageException(String message, Throwable cause) {
    super(message, cause);
  }
}

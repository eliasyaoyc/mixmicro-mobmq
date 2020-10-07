package xyz.vopen.framework.neptune.core.rpc.message;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * {@link RemoteHandshakeMessage}Handshake message between rpc endpoints. This message can be used
 * to verify compatibility between different endpoints.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class RemoteHandshakeMessage implements Serializable {

  private static final long serialVersionUID = 7535279881738899558L;

  private final @Nonnull Class<?> rpcGateway;

  private final @Nonnull int version;

  public RemoteHandshakeMessage(@Nonnull Class<?> rpcGateway, @Nonnull int version) {
    this.rpcGateway = rpcGateway;
    this.version = version;
  }

  public @Nonnull Class<?> getRpcGateway() {
    return this.rpcGateway;
  }

  public @Nonnull int getVersion() {
    return this.version;
  }
}

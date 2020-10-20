package xyz.vopen.framework.neptune.repository.api;

import xyz.vopen.framework.neptune.common.model.ServerInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * {@link ServerRepository} The repository interface for {@link ServerInfo}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/20
 */
public interface ServerRepository extends BaseRepository {

  /**
   * Returns the specifies server message through specifies the server name, allows null.
   *
   * @param serverName of the server.
   * @return The specifies server message.
   */
  Optional<ServerInfo> queryServerByName(@Nonnull String serverName);

  /**
   * Returns all servers message,allows null.
   *
   * @return The all servers message.
   */
  Optional<List<ServerInfo>> queryServers();

  /**
   * Save the server message.
   *
   * @param serverInfo {@link ServerInfo} instance.
   * @return
   */
  void saveServerInfo(@Nonnull ServerInfo serverInfo);
}

package xyz.vopen.framework.neptune.core.persistence.adapter;

import xyz.vopen.framework.neptune.common.model.ServerInfo;

import java.util.List;

/**
 * {@link PersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public interface PersistenceAdapter {

  ServerInfo queryServerByName(String serverName);

  List<ServerInfo> queryServers();

  void saveServerInfo(ServerInfo build);
}

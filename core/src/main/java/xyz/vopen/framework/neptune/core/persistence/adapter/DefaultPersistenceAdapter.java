package xyz.vopen.framework.neptune.core.persistence.adapter;

import com.google.common.base.Preconditions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.model.ServerInfo;
import xyz.vopen.framework.repository.mysql.MysqlRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * {@link DefaultPersistenceAdapter} Adapter {@link MysqlRepository}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DefaultPersistenceAdapter implements PersistenceAdapter {

  private final @Nonnull MysqlRepository mysqlRepository;

  private DefaultPersistenceAdapter(final Configuration configuration) {
    Preconditions.checkNotNull(configuration);
    this.mysqlRepository = MysqlRepository.createFromConfiguration(configuration);
  }

  public static DefaultPersistenceAdapter create(final Configuration configuration) {
    return new DefaultPersistenceAdapter(configuration);
  }

  @Override
  public ServerInfo queryServerByName(@Nonnull String serverName) {
    Optional<ServerInfo> serverInfo = mysqlRepository.queryServerByName(serverName);
    return serverInfo.get();
  }

  @Override
  public List<ServerInfo> queryServers() {
    Optional<List<ServerInfo>> serverInfos = mysqlRepository.queryServers();
    return serverInfos.get();
  }

  @Override
  public void saveServerInfo(@Nonnull ServerInfo serverInfo) {
    int i = mysqlRepository.saveServerInfo(serverInfo);
  }
}

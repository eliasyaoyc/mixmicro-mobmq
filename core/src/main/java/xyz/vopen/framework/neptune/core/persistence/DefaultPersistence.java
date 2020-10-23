package xyz.vopen.framework.neptune.core.persistence;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.configuration.JobManagerOptions;
import xyz.vopen.framework.neptune.common.model.ServerInfo;
import xyz.vopen.framework.neptune.core.persistence.adapter.PersistenceAdapter;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * {@link DefaultPersistence} Implementation for {@link Persistence}.Provides the ability of
 * initialize, persist.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DefaultPersistence implements Persistence {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultPersistence.class);

  private final @Nonnull Configuration configuration;
  private final @Nonnull PersistenceAdapter persistenceAdapter;

  private DefaultPersistence(
      final @Nonnull Configuration configuration,
      final @Nonnull PersistenceAdapter persistenceAdapter) {
    this.persistenceAdapter = persistenceAdapter;
    this.configuration = configuration;
    initialize();
  }

  public static Persistence create(
      Configuration configuration, PersistenceAdapter persistenceAdapter) {
    return new DefaultPersistence(configuration, persistenceAdapter);
  }

  @Override
  public void initialize() {
    String serverName = configuration.getString(JobManagerOptions.NAME);
    Preconditions.checkNotNull(serverName, "Server name is empty.");

    Optional<ServerInfo> serverInfo = persistenceAdapter.queryServerByName(serverName);
    if (!serverInfo.isPresent()) {
      // ignore
    } else {
      persistenceAdapter.saveServerInfo(ServerInfo.builder().build());
    }
  }

  @Override
  public PersistenceAdapter getPersistenceAdapter() {
    return this.persistenceAdapter;
  }
}

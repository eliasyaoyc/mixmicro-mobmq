package xyz.vopen.framework.neptune.core.persistence.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * {@link MongoPersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class MongoPersistenceAdapter implements PersistenceAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MongoPersistenceAdapter.class);

  private final @Nonnull Configuration configuration;

  private MongoPersistenceAdapter(Configuration configuration) {
    this.configuration = configuration;

    initialize();
  }

  public static MongoPersistenceAdapter create(final @Nonnull Configuration configuration) {
    return new MongoPersistenceAdapter(configuration);
  }

  @Override
  public void initialize() {}

  @Override
  public CompletableFuture<Void> persist() {
    return null;
  }
}

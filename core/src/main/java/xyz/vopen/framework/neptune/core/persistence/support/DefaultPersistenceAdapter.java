package xyz.vopen.framework.neptune.core.persistence.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * {@link DefaultPersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DefaultPersistenceAdapter implements PersistenceAdapter{
  private static final Logger logger = LoggerFactory.getLogger(DefaultPersistenceAdapter.class);

  public final Configuration configuration;

  private DefaultPersistenceAdapter(final Configuration configuration) {
    this.configuration = configuration;

    initialize();
  }

  public static DefaultPersistenceAdapter create(final Configuration configuration) {
    return new DefaultPersistenceAdapter(configuration);
  }


  @Override
  public void initialize() {

  }

  @Override
  public CompletableFuture<Void> persist() {
    return null;
  }


}

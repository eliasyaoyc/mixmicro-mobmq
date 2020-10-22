package xyz.vopen.framework.neptune.core.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

/**
 * {@link StandaloneNeptuneEntrypoint} Default Neptune entry points. Start as a service alone,
 * corresponding {@link EmbedNeptuneEntrypoint} which can be embedded in an existing service for
 * startup.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
public class StandaloneNeptuneEntrypoint extends NeptuneEntrypoint {
  private static final Logger LOG = LoggerFactory.getLogger(StandaloneNeptuneEntrypoint.class);

  public StandaloneNeptuneEntrypoint(Configuration configuration) {
    super(configuration);
  }
}

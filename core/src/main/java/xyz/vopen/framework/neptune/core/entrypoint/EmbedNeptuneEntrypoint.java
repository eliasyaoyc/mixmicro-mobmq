package xyz.vopen.framework.neptune.core.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

/**
 * {@link EmbedNeptuneEntrypoint} Embed model The lifecycle of the entrypoints is bound to that of
 * the specific application being executed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class EmbedNeptuneEntrypoint extends NeptuneEntrypoint {
  private static final Logger LOG = LoggerFactory.getLogger(EmbedNeptuneEntrypoint.class);

  public EmbedNeptuneEntrypoint(Configuration configuration) {
    super(configuration);
  }
}

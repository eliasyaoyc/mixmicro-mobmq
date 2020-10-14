package xyz.vopen.framework.neptune.core.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;

/**
 * {@link DefaultNeptuneEntrypoint} Default Neptune entry points. The lifecycle of the entrypoints
 * is bound to that of the specific application being executed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class DefaultNeptuneEntrypoint extends NeptuneEntrypoint {
  private static final Logger logger = LoggerFactory.getLogger(DefaultNeptuneEntrypoint.class);

  public DefaultNeptuneEntrypoint(Configuration configuration) {
    super(configuration);
  }
}

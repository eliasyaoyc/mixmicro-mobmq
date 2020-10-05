package xyz.vopen.framework.scheduler.core.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static xyz.vopen.framework.scheduler.core.autoconfigure.SchedulerCoreProperties.MIXMICRO_SCHEDULER_PROPERTIES_PREFIX;

/**
 * {@link SchedulerCoreProperties}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@ConfigurationProperties(prefix = MIXMICRO_SCHEDULER_PROPERTIES_PREFIX)
public class SchedulerCoreProperties {
  static final String MIXMICRO_SCHEDULER_PROPERTIES_PREFIX = "mixmicro.scheduler";
}

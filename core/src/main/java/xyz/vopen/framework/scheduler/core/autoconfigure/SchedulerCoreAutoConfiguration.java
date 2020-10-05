package xyz.vopen.framework.scheduler.core.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static xyz.vopen.framework.scheduler.core.autoconfigure.SchedulerCoreProperties.MIXMICRO_SCHEDULER_PROPERTIES_PREFIX;

/**
 * {@link SchedulerCoreAutoConfiguration}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@Configuration
@EnableConfigurationProperties(SchedulerCoreProperties.class)
@ConditionalOnProperty(
    prefix = MIXMICRO_SCHEDULER_PROPERTIES_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SchedulerCoreAutoConfiguration {
  private static final Logger logger =
      LoggerFactory.getLogger(SchedulerCoreAutoConfiguration.class);
}

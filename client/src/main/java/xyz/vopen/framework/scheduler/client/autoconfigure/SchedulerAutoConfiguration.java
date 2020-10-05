package xyz.vopen.framework.scheduler.client.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static xyz.vopen.framework.scheduler.client.autoconfigure.SchedulerProperties.MIXMIRO_SCHEDULER_CLIENT_PROPERTIES_PREFIX;

/**
 * {@link SchedulerAutoConfiguration}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@Configuration
@EnableConfigurationProperties(SchedulerProperties.class)
@ConditionalOnProperty(
    prefix = MIXMIRO_SCHEDULER_CLIENT_PROPERTIES_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SchedulerAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(SchedulerAutoConfiguration.class);

}

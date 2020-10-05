package xyz.vopen.framework.scheduler.client.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static xyz.vopen.framework.scheduler.client.autoconfigure.SchedulerProperties.MIXMIRO_SCHEDULER_CLIENT_PROPERTIES_PREFIX;

/**
 * {@link SchedulerProperties}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@ConfigurationProperties(prefix = MIXMIRO_SCHEDULER_CLIENT_PROPERTIES_PREFIX)
public class SchedulerProperties {
    static final String MIXMIRO_SCHEDULER_CLIENT_PROPERTIES_PREFIX = "mixmicro.scheduler.client";
}

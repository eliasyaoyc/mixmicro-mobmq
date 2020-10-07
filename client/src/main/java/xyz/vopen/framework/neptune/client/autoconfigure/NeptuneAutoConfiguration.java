package xyz.vopen.framework.neptune.client.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * {@link NeptuneAutoConfiguration}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@Configuration
@EnableConfigurationProperties(NeptuneProperties.class)
@ConditionalOnProperty(
    prefix = NeptuneProperties.MIXMIRO_NEPTUNE_CLIENT_PROPERTIES_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class NeptuneAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(NeptuneAutoConfiguration.class);

}

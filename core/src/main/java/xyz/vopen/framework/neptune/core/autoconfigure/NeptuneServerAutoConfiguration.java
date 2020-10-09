package xyz.vopen.framework.neptune.core.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static xyz.vopen.framework.neptune.core.autoconfigure.NeptuneServerProperties.MIXMICRO_NEPTUNE_SERVER_PROPERTIES_PREFIX;

/**
 * {@link NeptuneServerAutoConfiguration}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@Configuration
@EnableConfigurationProperties(NeptuneServerProperties.class)
@ConditionalOnProperty(
    prefix = MIXMICRO_NEPTUNE_SERVER_PROPERTIES_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class NeptuneServerAutoConfiguration {
  private static final Logger logger =
      LoggerFactory.getLogger(NeptuneServerAutoConfiguration.class);

}

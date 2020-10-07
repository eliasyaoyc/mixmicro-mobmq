package xyz.vopen.framework.neptune.client.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static xyz.vopen.framework.neptune.client.autoconfigure.NeptuneProperties.MIXMIRO_NEPTUNE_CLIENT_PROPERTIES_PREFIX;

/**
 * {@link NeptuneProperties}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@ConfigurationProperties(prefix = MIXMIRO_NEPTUNE_CLIENT_PROPERTIES_PREFIX)
public class NeptuneProperties {
    static final String MIXMIRO_NEPTUNE_CLIENT_PROPERTIES_PREFIX = "mixmicro.neptune.client";
}

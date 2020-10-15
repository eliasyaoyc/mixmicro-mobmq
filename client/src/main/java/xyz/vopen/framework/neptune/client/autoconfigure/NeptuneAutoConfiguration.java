package xyz.vopen.framework.neptune.client.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.vopen.framework.neptune.client.NeptuneClient;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;

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

  @Bean
  ApplicationReadyEventListener applicationReadyEventListener(
      final NeptuneProperties neptuneProperties) {
    return new ApplicationReadyEventListener(neptuneProperties);
  }

  class ApplicationReadyEventListener implements ApplicationListener<SpringApplicationEvent> {
    final NeptuneProperties neptuneProperties;

    ApplicationReadyEventListener(final NeptuneProperties neptuneProperties) {
      this.neptuneProperties = neptuneProperties;
    }

    @Override
    public void onApplicationEvent(SpringApplicationEvent springApplicationEvent) {
      if (springApplicationEvent instanceof ApplicationReadyEvent) {
        logger.info("[NeptuneClient] Starting....");
        NeptuneClient neptuneClient = new NeptuneClient(neptuneProperties);

        Runtime.getRuntime()
            .addShutdownHook(
                new Thread(
                    () -> {
                      try {
                        Thread.sleep(5000L);
                      } catch (InterruptedException e) {
                        
                      }
                      Runtime.getRuntime().halt(-17);
                    }));

        try {
          neptuneClient.init();
          neptuneClient.start();
        } catch (Exception e) {
          logger.error(
              "[NeptuneClient] Start failure, err : {}", ExceptionUtil.stringifyException(e));
          System.exit(1);
        }
      }
    }
  }
}

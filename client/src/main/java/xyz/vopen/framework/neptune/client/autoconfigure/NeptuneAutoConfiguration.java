package xyz.vopen.framework.neptune.client.autoconfigure;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.vopen.framework.neptune.client.NeptuneClientEntrypoint;
import xyz.vopen.framework.neptune.common.utils.time.Time;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.rpc.RpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcServiceConfiguration;

import javax.annotation.Nonnull;

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

  private static final String NEPTUNE_CLIENT_NAME = "neptune-client";
  private static final String NEPTUNE_CLIENT_CONFIG = "default.neptune.client.akka.conf";

  @Bean
  ApplicationReadyEventListener applicationReadyEventListener(
      final NeptuneProperties neptuneProperties) {
    return new ApplicationReadyEventListener(neptuneProperties);
  }

  class ApplicationReadyEventListener implements ApplicationListener<SpringApplicationEvent> {
    final NeptuneProperties neptuneProperties;
    final RpcService rpcService;

    ApplicationReadyEventListener(final @Nonnull NeptuneProperties neptuneProperties) {
      this.neptuneProperties = neptuneProperties;
      this.rpcService =
          new AkkaRpcService(
              ActorSystem.create(NEPTUNE_CLIENT_NAME, ConfigFactory.load(NEPTUNE_CLIENT_CONFIG)),
              new AkkaRpcServiceConfiguration(Time.seconds(10), 10485760, true));
    }

    @Override
    public void onApplicationEvent(SpringApplicationEvent springApplicationEvent) {
      if (springApplicationEvent instanceof ApplicationReadyEvent) {
        logger.info("[NeptuneClient] Starting....");
        NeptuneClientEntrypoint entrypoint =
            new NeptuneClientEntrypoint(neptuneProperties, rpcService, NEPTUNE_CLIENT_NAME);

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
          entrypoint.internalCallOnStart();
        } catch (Exception e) {
          logger.error(
              "[NeptuneClient] Start failure, err : {}", ExceptionUtil.stringifyException(e));
          System.exit(1);
        }
      }
    }
  }
}

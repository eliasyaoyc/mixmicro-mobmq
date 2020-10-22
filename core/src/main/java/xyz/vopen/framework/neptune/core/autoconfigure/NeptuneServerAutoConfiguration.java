package xyz.vopen.framework.neptune.core.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.common.configuration.JobManagerOptions;
import xyz.vopen.framework.neptune.common.configuration.command.CommandLineParser;
import xyz.vopen.framework.neptune.common.configuration.command.NeptuneConfiguration;
import xyz.vopen.framework.neptune.common.configuration.command.NeptuneConfigurationParserFactory;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneParseException;
import xyz.vopen.framework.neptune.core.entrypoint.NeptuneEntrypoint;
import xyz.vopen.framework.neptune.core.entrypoint.StandaloneNeptuneEntrypoint;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

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

  @Resource private ApplicationArguments arguments;

  @Bean
  ApplicationReadyEventListener applicationReadyEventListener(
      NeptuneServerProperties neptuneServerProperties) {
    return new ApplicationReadyEventListener(neptuneServerProperties);
  }

  class ApplicationReadyEventListener implements ApplicationListener<SpringApplicationEvent> {
    private final NeptuneServerProperties neptuneServerProperties;

    ApplicationReadyEventListener(final NeptuneServerProperties neptuneServerProperties) {
      this.neptuneServerProperties = neptuneServerProperties;
    }

    /**
     * Handle an application event.
     *
     * @param springApplicationEvent to response to.
     */
    @Override
    public void onApplicationEvent(@Nonnull SpringApplicationEvent springApplicationEvent) {
      if (springApplicationEvent instanceof ApplicationReadyEvent) {

        Runtime.getRuntime()
            .addShutdownHook(
                new Thread(
                    () -> {
                      try {
                        Thread.sleep(5000L);
                      } catch (Throwable e) {

                      }
                      Runtime.getRuntime().halt(-17);
                    }));

        CommandLineParser<NeptuneConfiguration> parser =
            new CommandLineParser<>(new NeptuneConfigurationParserFactory());
        NeptuneConfiguration neptuneConfiguration = null;

        try {
          String[] args = arguments.getSourceArgs();
          neptuneConfiguration = parser.parse(args);
        } catch (NeptuneParseException e) {
          logger.error("Could not parse command line arguments {}.", null, e);
          parser.printHelp(NeptuneConfiguration.class.getSimpleName());
          System.exit(1);
        }

        NeptuneEntrypoint entrypoint =
            new StandaloneNeptuneEntrypoint(
                loadConfigurationFromNeptuneConfig(neptuneConfiguration, neptuneServerProperties));

        NeptuneEntrypoint.runApplicationEntrypoint(entrypoint);
      }
    }
  }

  @VisibleForTesting
  static xyz.vopen.framework.neptune.common.configuration.Configuration
      loadConfigurationFromNeptuneConfig(
          NeptuneConfiguration neptuneConf, @Nonnull NeptuneServerProperties properties) {
    final xyz.vopen.framework.neptune.common.configuration.Configuration configuration =
        new xyz.vopen.framework.neptune.common.configuration.Configuration();

    // job manager rpc address.
    if (StringUtils.isNotEmpty(neptuneConf.getJobManagerRpcAddress())) {
      configuration.setString(JobManagerOptions.ADDRESS, neptuneConf.getJobManagerRpcAddress());
    } else {
      configuration.setString(JobManagerOptions.ADDRESS, properties.getJobManagerRpcAddress());
    }

    // job manager rpc port.
    if (StringUtils.isNotEmpty(neptuneConf.getJobManagerRpcPort())) {
      configuration.setString(JobManagerOptions.PORT, neptuneConf.getJobManagerRpcPort());
    } else if (StringUtils.isNotEmpty(properties.getJobManagerRpcPort())) {
      configuration.setString(JobManagerOptions.PORT, properties.getJobManagerRpcPort());
    }

    // job manager bind host.
    if (StringUtils.isNotEmpty(neptuneConf.getJobManagerBindHost())) {
      configuration.setString(JobManagerOptions.BIND_HOST, neptuneConf.getJobManagerBindHost());
    } else if (StringUtils.isNotEmpty(properties.getJobManagerBindHost())) {
      configuration.setString(JobManagerOptions.BIND_HOST, properties.getJobManagerBindHost());
    }

    // job manager rpc bind port.
    if (StringUtils.isNotEmpty(neptuneConf.getJobManagerRpcBindPort())) {
      configuration.setString(
          JobManagerOptions.RPC_BIND_PORT, neptuneConf.getJobManagerRpcBindPort());
    } else if (StringUtils.isNotEmpty(properties.getJobManagerRpcBindPort())) {
      configuration.setString(
          JobManagerOptions.RPC_BIND_PORT, properties.getJobManagerRpcBindPort());
    }

    return configuration;
  }
}

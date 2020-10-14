package xyz.vopen.framework.neptune.core.autoconfigure;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.core.dispatcher.DefaultDispatcherFactory;
import xyz.vopen.framework.neptune.core.dispatcher.Dispatcher;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.rpc.RpcGateway;
import xyz.vopen.framework.neptune.rpc.RpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcServiceConfiguration;
import xyz.vopen.framework.neptune.rpc.akka.AkkaUtils;

import javax.annotation.Nonnull;

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
        AkkaRpcServiceConfiguration configuration =
            AkkaRpcServiceConfiguration.defaultConfiguration();
        ActorSystem actorSystem = AkkaUtils.createDefaultActorSystem();
        RpcService rpcService = new AkkaRpcService(actorSystem, configuration);

        Dispatcher dispatcher =
            DefaultDispatcherFactory.INSTANCE.create(
                new xyz.vopen.framework.neptune.common.configuration.Configuration(),
                new FatalErrorHandler() {
                  @Override
                  public void onFatalError(Throwable exception) {
                    System.out.println(ExceptionUtil.stringifyException(exception));
                  }
                },
                rpcService,
                "test");

        HelloEndpoint helloEndpoint = new HelloEndpoint(rpcService);
        helloEndpoint.start();
        HelloGateway selfGateway = helloEndpoint.getSelfGateway(HelloGateway.class);
        String hello = selfGateway.hello();
        System.out.println(hello);
      }
    }
  }

  public static class HelloEndpoint extends RpcEndpoint implements HelloGateway {

    protected HelloEndpoint(RpcService rpcService) {
      super(rpcService);
    }

    @Override
    public String hello() {
      return "hello";
    }
  }

  public interface HelloGateway extends RpcGateway {
    String hello();
  }
}

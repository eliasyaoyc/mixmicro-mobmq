package xyz.vopen.framework.neptune.client;

import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import xyz.vopen.framework.neptune.client.actors.SlotActor;
import xyz.vopen.framework.neptune.client.actors.ThrowableActor;
import xyz.vopen.framework.neptune.client.autoconfigure.NeptuneProperties;
import xyz.vopen.framework.neptune.common.AutoCloseableAsync;
import xyz.vopen.framework.neptune.common.utils.NetUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link NeptuneClient}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public class NeptuneClient implements ApplicationContextAware, AutoCloseableAsync {
  private static final Logger logger = LoggerFactory.getLogger(NeptuneClient.class);

  private static final String NEPTUNE_CLIENT_AKKA_CONF = "neptune-client.akka.conf";

  private final @Nonnull NeptuneProperties neptuneProperties;
  private final CompletableFuture<Void> terminatedFuture;
  private Config config;
  private AtomicBoolean isShutDown = new AtomicBoolean(false);
  private static final int ACTOR_SYSTEM_PROCESSOR = Runtime.getRuntime().availableProcessors();
  private ActorSystem actorSystem;

  public NeptuneClient(final @Nonnull NeptuneProperties neptuneProperties) {
    this.terminatedFuture = new CompletableFuture<>();
    this.neptuneProperties = neptuneProperties;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextUtil.inject(applicationContext);
  }

  public void init() {
    Map<String, Object> overrideConfig = Maps.newHashMap();
    overrideConfig.put("akka.remote.aretry.cononical.hostname", NetUtils.getLocalHost());
    overrideConfig.put("akka.remote.aretry.cononical.port", neptuneProperties.getPort());

    Config basicConfig = ConfigFactory.load(NEPTUNE_CLIENT_AKKA_CONF);
    config = ConfigFactory.parseMap(overrideConfig).withFallback(basicConfig);
  }

  public void start() {
    Stopwatch stopwatch = Stopwatch.createStarted();
    Preconditions.checkNotNull(this.config, "Akka config is empty.");
    try {
      // start akka.
      actorSystem = ActorSystem.create("neptune-worker", this.config);
      actorSystem.actorOf(
          Props.create(SlotActor.class)
              .withDispatcher("akka.slot-dispatcher")
              .withRouter(new RoundRobinPool(ACTOR_SYSTEM_PROCESSOR * 2)),
          "slot_actor");

      // process exception in system.
      actorSystem
          .eventStream()
          .subscribe(
              actorSystem.actorOf(Props.create(ThrowableActor.class), "throwable_actor"),
              DeadLetter.class);

      logger.info(
          "[NeptuneClient] Akka actorSystem({}) initialized successfully, akka-remote listening address : {}",
          actorSystem,
          NetUtils.getLocalHost() + ":" + neptuneProperties.getPort());

      // to do.
      logger.info("[NeptuneClient] started successfully, using time : {}", stopwatch);
    } catch (Exception e) {
      logger.error("[NeptuneClient] started failed, using time : {}, err : {}", stopwatch, e);
      shutdownAsync();
    }
  }

  /** @return */
  public CompletableFuture<Void> stop() {
    return null;
  }

  public CompletableFuture<Void> getTerminationFuture() {
    return this.terminatedFuture;
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return shutdownAsync().thenAccept(ignored -> {});
  }

  private CompletableFuture<Void> shutdownAsync() {
    if (isShutDown.compareAndSet(false, true)) {

      logger.info("[NeptuneClient] Stopping...");

      CompletableFuture<Void> shutdownFuture = this.stop();

      shutdownFuture.whenComplete(
          (Void ignored, Throwable throwable) -> {
            if (throwable != null) {
              terminatedFuture.completeExceptionally(throwable);
            } else {
              terminatedFuture.complete(ignored);
            }
          });
    }
    return terminatedFuture;
  }
}

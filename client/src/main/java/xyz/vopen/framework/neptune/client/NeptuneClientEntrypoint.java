package xyz.vopen.framework.neptune.client;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import xyz.vopen.framework.neptune.client.autoconfigure.NeptuneProperties;
import xyz.vopen.framework.neptune.common.AutoCloseableAsync;
import xyz.vopen.framework.neptune.common.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.rpc.RpcService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link NeptuneClientEntrypoint}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public class NeptuneClientEntrypoint extends RpcEndpoint
    implements ApplicationContextAware, AutoCloseableAsync {
  private static final Logger LOG = LoggerFactory.getLogger(NeptuneClientEntrypoint.class);

  private final @Nonnull NeptuneProperties neptuneProperties;
  private final CompletableFuture<Void> terminatedFuture;
  private AtomicBoolean isShutDown = new AtomicBoolean(false);
  private final @Nonnull RpcService rpcService;
  private final Object lock = new Object();

  public NeptuneClientEntrypoint(
      final @Nonnull NeptuneProperties neptuneProperties,
      final @Nonnull RpcService rpcService,
      final @Nonnull String endpointId) {
    super(rpcService, endpointId);
    this.rpcService = rpcService;
    this.neptuneProperties = neptuneProperties;
    this.terminatedFuture = new CompletableFuture<>();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextUtil.inject(applicationContext);
  }

  @Override
  protected void onStart() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
//    Preconditions.checkNotNull(this.config, "Akka config is empty.");
    try {
      // start akka.
//      actorSystem = ActorSystem.create("neptune-client", this.config);
//
//      actorSystem.actorOf(
//          Props.create(TaskManagerActor.class)
//              .withDispatcher("akka.task-manager-dispatcher")
//              .withRouter(new RoundRobinPool(ACTOR_SYSTEM_PROCESSOR * 2)),
//          "task_manager_actor");
//
//      actorSystem.actorOf(
//          Props.create(SlotActor.class)
//              .withDispatcher("akka.slot-dispatcher")
//              .withRouter(new RoundRobinPool(ACTOR_SYSTEM_PROCESSOR)),
//          "slot_actor");
//
//      // process exception in system.
//      ActorRef throwableActor =
//          actorSystem.actorOf(Props.create(ThrowableActor.class), "throwable_actor");
//      actorSystem.eventStream().subscribe(throwableActor, DeadLetter.class);
//
//      logger.info(
//          "[NeptuneClient] Akka actorSystem({}) initialized successfully, akka-remote listening address : {}",
//          actorSystem,
//          NetUtils.getLocalHost() + ":" + neptuneProperties.getPort());


      // start rpc endpoint.
      super.start();




      LOG.info("[NeptuneClient] started successfully, using time : {}", stopwatch);
    } catch (Exception e) {
      LOG.error("[NeptuneClient] started failed, using time : {}, err : {}", stopwatch, e);
      shutdownAsync();
    }
  }

  @Override
  protected CompletableFuture<Void> onStop() {
    LOG.info("Stopping dispatcher {} .", getAddress());
    return super.onStop();
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

      LOG.info("[NeptuneClient] Stopping...");

      CompletableFuture<Void> shutdownFuture = this.stopService();

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

  private CompletableFuture<Void> stopService() {
    synchronized (lock) {
      Throwable exception = null;

      final Collection<CompletableFuture<Void>> terminationFutures = new ArrayList<>(3);

      return FutureUtil.completeAll(terminationFutures);
    }
  }
}

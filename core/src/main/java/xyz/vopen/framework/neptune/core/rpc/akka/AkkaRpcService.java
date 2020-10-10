package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.*;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.Tuple2;
import scala.concurrent.Future;
import scala.reflect.ClassTag$;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.common.utils.ExecutorThreadFactory;
import xyz.vopen.framework.neptune.common.utils.ExecutorStUtil;
import xyz.vopen.framework.neptune.common.utils.TimeUtil;
import xyz.vopen.framework.neptune.core.concurrent.ActorSystemScheduledExecutorAdapter;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.core.concurrent.ScheduledExecutor;
import xyz.vopen.framework.neptune.core.exceptions.rpc.RpcConnectionException;
import xyz.vopen.framework.neptune.core.exceptions.rpc.RpcRuntimeException;
import xyz.vopen.framework.neptune.core.rpc.*;
import xyz.vopen.framework.neptune.core.rpc.message.HandshakeSuccessMessage;
import xyz.vopen.framework.neptune.core.rpc.message.RemoteHandshakeMessage;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * {@link AkkaRpcService} Based {@link RpcService} implementation. The RPC service starts an Akka
 * actor to receive RPC invocation from a {@link RpcGateway}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
@ThreadSafe
public class AkkaRpcService implements RpcService {
  private static final Logger logger = LoggerFactory.getLogger(AkkaRpcService.class);

  static final int VERSION = 2;

  private final Object lock = new Object();

  private final ActorSystem actorSystem;
  private final AkkaRpcServiceConfiguration configuration;

  @GuardedBy("lock")
  private final Map<ActorRef, RpcEndpoint> actors = new HashMap<>(4);

  private final String address;
  private final int port;

  private final boolean captureAskCallstacks;

  private final ScheduledExecutor internalScheduledExecutor;

  private final CompletableFuture<Void> terminationFuture;

  private final Supervisor supervisor;

  private volatile boolean stopped;

  @VisibleForTesting
  public AkkaRpcService(
      final ActorSystem actorSystem, final AkkaRpcServiceConfiguration configuration) {
    this.actorSystem = Preconditions.checkNotNull(actorSystem, "actor system");
    this.configuration =
        Preconditions.checkNotNull(configuration, "akka rpc service configuration");

    Address actorSystemAddress = AkkaUtils.getAddress(actorSystem);

    if (actorSystemAddress.host().isDefined()) {
      address = actorSystemAddress.host().get();
    } else {
      address = "";
    }

    if (actorSystemAddress.port().isDefined()) {
      port = (Integer) actorSystemAddress.port().get();
    } else {
      port = -1;
    }

    captureAskCallstacks = configuration.captureAskCallStack();

    internalScheduledExecutor = new ActorSystemScheduledExecutorAdapter(actorSystem);

    terminationFuture = new CompletableFuture<>();

    stopped = false;

    supervisor = startSupervisorActor();
  }

  private Supervisor startSupervisorActor() {
    final ExecutorService terminationFutureExecutor =
        Executors.newSingleThreadExecutor(
            new ExecutorThreadFactory("AkkaRpcService-Supervisor-Termination-Future-Executor"));
    final ActorRef actorRef =
        SupervisorActor.startSupervisorActor(actorSystem, terminationFutureExecutor);

    return Supervisor.create(actorRef, terminationFutureExecutor);
  }

  public ActorSystem getActorSystem() {
    return this.actorSystem;
  }

  protected int getVersion() {
    return VERSION;
  }

  @Override
  public String getAddress() {
    return this.address;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public <C extends RpcGateway> CompletableFuture<C> connect(String address, Class<C> clazz) {
    return connectInternal(
        address,
        clazz,
        (ActorRef actorRef) -> {
          Tuple2<String, String> addressHostname = extractAddressHostname(actorRef);

          return new AkkaInvocationHandler(
              addressHostname._1,
              addressHostname._2,
              actorRef,
              configuration.getTimeout(),
              configuration.getMaximumFrameSize(),
              null,
              captureAskCallstacks);
        });
  }

  /**
   * Start a rpc server which forwards the remote procedure calls to the provided rpc endpoint.
   *
   * @param rpcEndpoint Rpc protocol to dispatch the rpcs to
   * @param <C> Type of the rpc endpoint
   * @return Self gateway to dispatch remote procedure calls to oneself.
   */
  @Override
  public <C extends RpcEndpoint & RpcGateway> RpcServer startServer(C rpcEndpoint) {
    Preconditions.checkNotNull(rpcEndpoint, "rpc endpoint");

    final SupervisorActor.ActorRegistration actorRegistration = registerAkkaRpcActor(rpcEndpoint);

    final ActorRef actorRef = actorRegistration.getActorRef();
    final CompletableFuture<Void> actorTerminationFuture = actorRegistration.getTerminationFuture();

    logger.info(
        "Starting RPC endpoint for {} at {} .", rpcEndpoint.getClass().getName(), actorRef.path());

    final String akkaAddress = AkkaUtils.getAkkaURL(actorSystem, actorRef);
    final String hostname;
    Option<String> host = actorRef.path().address().host();
    if (host.isEmpty()) {
      hostname = "localhost";
    } else {
      hostname = host.get();
    }

    // proxy interface.
    Set<Class<?>> implementedRpcGateways =
        new HashSet<>(RpcUtil.extractImplementedRpcGateways(rpcEndpoint.getClass()));

    implementedRpcGateways.add(RpcServer.class);
    implementedRpcGateways.add(AkkaBasedEndpoint.class);

    final InvocationHandler invocationHandler =
        new AkkaInvocationHandler(
            akkaAddress,
            hostname,
            actorRef,
            configuration.getTimeout(),
            configuration.getMaximumFrameSize(),
            actorTerminationFuture,
            captureAskCallstacks);

    // Rather than using the System ClassLoader directly, we derive the ClassLoader
    // from this class . That works better in cases where Neptune runs embedded and all Neptune
    // code is loaded dynamically (for example from an OSGI bundle) through a custom ClassLoader
    ClassLoader classLoader = getClass().getClassLoader();

    RpcServer server =
        (RpcServer)
            Proxy.newProxyInstance(
                classLoader,
                implementedRpcGateways.toArray(new Class<?>[implementedRpcGateways.size()]),
                invocationHandler);
    return server;
  }

  private <C extends RpcEndpoint & RpcGateway>
      SupervisorActor.ActorRegistration registerAkkaRpcActor(C rpcEndpoint) {
    final Class<? extends AbstractActor> akkaRpcActorType = AkkaRpcActor.class;

    synchronized (this.lock) {
      Preconditions.checkState(!stopped, "RpcService is stopped");

      final SupervisorActor.StartAkkaRpcActorResponse startAkkaRpcActorResponse =
          SupervisorActor.startAkkaRpcActor(
              supervisor.getActor(),
              actorTerminationFuture ->
                  Props.create(
                      akkaRpcActorType,
                      rpcEndpoint,
                      actorTerminationFuture,
                      getVersion(),
                      configuration.getMaximumFrameSize()),
              rpcEndpoint.getEndpointId());

      final SupervisorActor.ActorRegistration actorRegistration =
          startAkkaRpcActorResponse.orElseThrow(
              cause ->
                  new RpcRuntimeException(
                      String.format(
                          "Could not create the %s for %s.",
                          AkkaRpcActor.class.getSimpleName(), rpcEndpoint.getEndpointId()),
                      cause));

      actors.put(actorRegistration.getActorRef(), rpcEndpoint);

      return actorRegistration;
    }
  }

  @Override
  public void stopServer(RpcServer selfGateway) {
    if (selfGateway instanceof AkkaBasedEndpoint) {
      final AkkaBasedEndpoint akkaClient = (AkkaBasedEndpoint) selfGateway;
      final RpcEndpoint rpcEndpoint;

      synchronized (this.lock) {
        if (stopped) {
          return;
        } else {
          rpcEndpoint = actors.remove(akkaClient.getActorRef());
        }
      }
      if (rpcEndpoint != null) {
        terminateAkkaRpcActor(akkaClient.getActorRef(), rpcEndpoint);
      } else {
        logger.debug(
            "RPC endpoint {} already stopped or from differnet RPC service",
            selfGateway.getAddress());
      }
    }
  }

  @Override
  public CompletableFuture<Void> stopService() {
    final CompletableFuture<Void> akkaRpcActorsTerminationFuture;

    synchronized (lock) {
      if (stopped) {
        return terminationFuture;
      }

      logger.info("Stopping Akka RPC service.");

      stopped = true;

      akkaRpcActorsTerminationFuture = terminateAkkaRpcActors();
    }

    final CompletableFuture<Void> supervisorTerminationFuture =
        FutureUtil.composeAfterwards(akkaRpcActorsTerminationFuture, supervisor::closeAsync);

    final CompletableFuture<Void> actorSystemTerminationFuture =
        FutureUtil.composeAfterwards(
            supervisorTerminationFuture, () -> FutureUtil.toJava(actorSystem.terminate()));

    actorSystemTerminationFuture.whenComplete(
        (Void ignored, Throwable throwable) -> {
          if (throwable != null) {
            terminationFuture.completeExceptionally(throwable);
          } else {
            terminationFuture.complete(null);
          }

          logger.info("Stopped Akka RPC service.");
        });

    return terminationFuture;
  }

  @GuardedBy("lock")
  private @Nonnull CompletableFuture<Void> terminateAkkaRpcActors() {
    final Collection<CompletableFuture<Void>> akkaRpcActorTerminationFutures =
        new ArrayList<>(actors.size());

    for (Map.Entry<ActorRef, RpcEndpoint> actorRefRpcEndpointEntry : actors.entrySet()) {
      akkaRpcActorTerminationFutures.add(
          terminateAkkaRpcActor(
              actorRefRpcEndpointEntry.getKey(), actorRefRpcEndpointEntry.getValue()));
    }
    actors.clear();

    return FutureUtil.waitForAll(akkaRpcActorTerminationFutures);
  }

  private CompletableFuture<Void> terminateAkkaRpcActor(
      ActorRef akkaRpcActofRef, RpcEndpoint rpcEndpoint) {
    akkaRpcActofRef.tell(ControlMessages.TERMINATE, ActorRef.noSender());
    return rpcEndpoint.getTerminationFuture();
  }

  @Override
  public CompletableFuture<Void> getTerminationFuture() {
    return this.terminationFuture;
  }

  @Override
  public Executor getExecutor() {
    return this.actorSystem.dispatcher();
  }

  @Override
  public ScheduledExecutor getScheduledExecutor() {
    return this.internalScheduledExecutor;
  }

  @Override
  public ScheduledFuture<?> scheduleRunnable(Runnable runnable, long delay, TimeUnit unit) {
    Preconditions.checkNotNull(runnable, "runnable");
    Preconditions.checkNotNull(unit, "unit");
    Preconditions.checkArgument(delay > 0L, "delay must be zero or larger");

    return internalScheduledExecutor.schedule(runnable, delay, unit);
  }

  @Override
  public void execute(Runnable runnable) {
    this.actorSystem.dispatcher().execute(runnable);
  }

  @Override
  public <T> CompletableFuture<T> execute(Callable<T> callable) {
    Future<T> scalaFuture = Futures.<T>future(callable, actorSystem.dispatcher());
    return FutureUtil.toJava(scalaFuture);
  }

  private Tuple2<String, String> extractAddressHostname(ActorRef actorRef) {
    final String actorAddress = AkkaUtils.getAkkaURL(actorSystem, actorRef);
    final String hostname;
    Option<String> host = actorRef.path().address().host();
    if (host.isEmpty()) {
      hostname = "localhost";
    } else {
      hostname = host.get();
    }

    return Tuple2.apply(actorAddress, hostname);
  }

  private <C extends RpcGateway> CompletableFuture<C> connectInternal(
      final String address,
      final Class<C> clazz,
      Function<ActorRef, InvocationHandler> invocationHandlerFactory) {
    Preconditions.checkState(!stopped, "RpcService is stopped");

    logger.debug(
        "Try to connect to remote RPC endpoint with address {}. Returning a {} gateway.",
        address,
        clazz.getName());

    // 获取 actor 的引用 ActorRef
    final CompletableFuture<ActorRef> actorRefFuture = resolveActorAddress(address);

    // 发送握手消息
    final CompletableFuture<HandshakeSuccessMessage> handshakeFuture =
        actorRefFuture.thenCompose(
            (ActorRef actorRef) ->
                FutureUtil.toJava(
                    Patterns.ask(
                            actorRef,
                            new RemoteHandshakeMessage(clazz, getVersion()),
                            configuration.getTimeout().toMilliseconds())
                        .<HandshakeSuccessMessage>mapTo(
                            ClassTag$.MODULE$.<HandshakeSuccessMessage>apply(
                                HandshakeSuccessMessage.class))));

    // create InvocationHandler and generate proxy object through dynamic proxy.
    return actorRefFuture.thenCombineAsync(
        handshakeFuture,
        (ActorRef actorRef, HandshakeSuccessMessage ignored) -> {
          InvocationHandler invocationHandler = invocationHandlerFactory.apply(actorRef);

          // Rather than using the System ClassLoader directly, we derive the ClassLoader
          // from this class . That works better in cases where Flink runs embedded and all Flink
          // code is loaded dynamically (for example from an OSGI bundle) through a custom
          // ClassLoader
          ClassLoader classLoader = getClass().getClassLoader();

          @SuppressWarnings("unchecked")
          C proxy =
              (C) Proxy.newProxyInstance(classLoader, new Class<?>[] {clazz}, invocationHandler);

          return proxy;
        },
        actorSystem.dispatcher());
  }

  private CompletableFuture<ActorRef> resolveActorAddress(String address) {
    final ActorSelection actorSel = actorSystem.actorSelection(address);

    return actorSel
        .resolveOne(TimeUtil.toDuration(configuration.getTimeout()))
        .toCompletableFuture()
        .exceptionally(
            error -> {
              throw new CompletionException(
                  new RpcConnectionException(
                      String.format("Could not connect to rpc endpoint under address %s.", address),
                      error));
            });
  }

  private static final class Supervisor implements AutoCloseableAsync {
    private final ActorRef actor;

    private final ExecutorService terminationFutureExecutor;

    private Supervisor(ActorRef actor, ExecutorService terminationFutureExecutor) {
      this.actor = actor;
      this.terminationFutureExecutor = terminationFutureExecutor;
    }

    private static Supervisor create(ActorRef actorRef, ExecutorService terminationFutureExecutor) {
      return new Supervisor(actorRef, terminationFutureExecutor);
    }

    public ActorRef getActor() {
      return actor;
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
      return ExecutorStUtil.nonBlockingShutdown(30L, TimeUnit.SECONDS, terminationFutureExecutor);
    }
  }
}

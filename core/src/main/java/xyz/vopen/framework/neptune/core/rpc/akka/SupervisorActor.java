package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.AkkaException;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.Patterns;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.PartialFunction;
import scala.collection.Iterable;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.core.exceptions.rpc.AkkaUnknownMessageException;
import xyz.vopen.framework.neptune.core.exceptions.rpc.RpcException;
import xyz.vopen.framework.neptune.core.rpc.RpcUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * {@link SupervisorActor} Supervisor actor which is responsible for starting {@link AkkaRpcActor}
 * instances and monitoring when the actors have terminated.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class SupervisorActor extends AbstractActor {
  private static final Logger logger = LoggerFactory.getLogger(SupervisorActor.class);

  private final Executor terminationFutureExecutor;

  private final Map<ActorRef, AkkaRpcActorRegistration> registeredAkkaRpcActors;

  SupervisorActor(Executor terminationFutureExecutor) {
    this.terminationFutureExecutor = terminationFutureExecutor;
    this.registeredAkkaRpcActors = new HashMap<>();
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(StartAkkaRpcActor.class, this::createStartAkkaRpcActorMessage)
        .matchAny(this::handleUnknownMessage)
        .build();
  }

  @Override
  public void postStop() throws Exception {
    logger.debug("Stopping supervisor actor.");

    super.postStop();

    for (AkkaRpcActorRegistration actorRegistration : registeredAkkaRpcActors.values()) {
      terminateAkkaRpcActorOnStop(actorRegistration);
    }

    registeredAkkaRpcActors.clear();
  }

  @Override
  public SupervisorActorSupervisorStrategy supervisorStrategy() {
    return new SupervisorActorSupervisorStrategy();
  }

  private void terminateAkkaRpcActorOnStop(AkkaRpcActorRegistration akkaRpcActorRegistration) {
    akkaRpcActorRegistration.terminateExceptionally(
        new RpcException(
            String.format(
                "Unexpected closing of %s with name %s.",
                getClass().getSimpleName(), akkaRpcActorRegistration.getEndpointId())),
        terminationFutureExecutor);
  }

  private void createStartAkkaRpcActorMessage(StartAkkaRpcActor startAkkaRpcActor) {
    final String endpointId = startAkkaRpcActor.getEndpointId();
    final AkkaRpcActorRegistration akkaRpcActorRegistration =
        new AkkaRpcActorRegistration(endpointId);

    final Props akkaRpcActorProps =
        startAkkaRpcActor
            .getPropsFactory()
            .create(akkaRpcActorRegistration.getInternalTerminationFuture());

    logger.debug(
        "Starting {} with name {}.", akkaRpcActorProps.actorClass().getSimpleName(), endpointId);

    try {
      final ActorRef actorRef = getContext().actorOf(akkaRpcActorProps, endpointId);

      registeredAkkaRpcActors.put(actorRef, akkaRpcActorRegistration);

      getSender()
          .tell(
              StartAkkaRpcActorResponse.success(
                  ActorRegistration.create(
                      actorRef, akkaRpcActorRegistration.getExternalTerminationFuture())),
              getSelf());
    } catch (AkkaException akkaException) {
      getSender().tell(StartAkkaRpcActorResponse.failure(akkaException), getSelf());
    }
  }

  private void akkaRpcActorTerminated(ActorRef actorRef) {
    final AkkaRpcActorRegistration actorRegistration = removeAkkaRpcActor(actorRef);

    logger.debug("AkkaRpcActor {} has terminated.", actorRef.path());
    actorRegistration.terminate(terminationFutureExecutor);
  }

  private void akkaRpcActorFailed(ActorRef actorRef, Throwable cause) {
    logger.warn("AkkaRpcActor {} has failed. Shutting it down now.", actorRef.path(), cause);

    for (Map.Entry<ActorRef, AkkaRpcActorRegistration> registeredAkkaRpcActor :
        registeredAkkaRpcActors.entrySet()) {
      final ActorRef otherActorRef = registeredAkkaRpcActor.getKey();
      if (otherActorRef.equals(actorRef)) {
        final RpcException error =
            new RpcException(
                String.format("Stopping actor %s because it failed.", actorRef.path()), cause);
        registeredAkkaRpcActor.getValue().markFailed(error);
      } else {
        final RpcException siblingException =
            new RpcException(
                String.format(
                    "Stopping actor %s because its sibling %s has failed.",
                    otherActorRef.path(), actorRef.path()));
        registeredAkkaRpcActor.getValue().markFailed(siblingException);
      }
    }

    getContext().getSystem().terminate();
  }

  private AkkaRpcActorRegistration removeAkkaRpcActor(ActorRef actorRef) {
    return Optional.ofNullable(registeredAkkaRpcActors.remove(actorRef))
        .orElseThrow(
            () ->
                new IllegalStateException(
                    String.format("Could not find actor %s.", actorRef.path())));
  }

  private void handleUnknownMessage(Object msg) {
    final AkkaUnknownMessageException cause =
        new AkkaUnknownMessageException(String.format("Cannot handle unknown message %s.", msg));
    getSender().tell(new akka.actor.Status.Failure(cause), getSelf());
    throw cause;
  }

  public static String getActorName() {
    return AkkaRpcServiceUtils.SUPERVISOR_NAME;
  }

  public static ActorRef startSupervisorActor(
      ActorSystem actorSystem, Executor terminationFutureExecutor) {
    final Props supervisorProps =
        Props.create(SupervisorActor.class, terminationFutureExecutor)
            .withDispatcher("akka.actor.supervisor-dispatcher");
    return actorSystem.actorOf(supervisorProps, getActorName());
  }

  public static StartAkkaRpcActorResponse startAkkaRpcActor(
      ActorRef supervisor, StartAkkaRpcActor.PropsFactory propsFactory, String endpointId) {
    return Patterns.ask(
            supervisor,
            createStartAkkaRpcActorMessage(propsFactory, endpointId),
            RpcUtil.INF_DURATION)
        .toCompletableFuture()
        .thenApply(SupervisorActor.StartAkkaRpcActorResponse.class::cast)
        .join();
  }

  public static StartAkkaRpcActor createStartAkkaRpcActorMessage(
      StartAkkaRpcActor.PropsFactory propsFactory, String endpointId) {
    return StartAkkaRpcActor.create(propsFactory, endpointId);
  }

  // -----------------------------------------------------------------------------
  // Internal classes
  // -----------------------------------------------------------------------------

  private final class SupervisorActorSupervisorStrategy extends SupervisorStrategy {

    @Override
    public PartialFunction<Throwable, Directive> decider() {
      return DeciderBuilder.match(Exception.class, e -> (Directive) SupervisorStrategy.stop())
          .build();
    }

    @Override
    public boolean loggingEnabled() {
      return false;
    }

    @Override
    public void handleChildTerminated(
        akka.actor.ActorContext context, ActorRef child, Iterable<ActorRef> children) {
      akkaRpcActorTerminated(child);
    }

    @Override
    public void processFailure(
        akka.actor.ActorContext context,
        boolean restart,
        ActorRef child,
        Throwable cause,
        ChildRestartStats stats,
        Iterable<ChildRestartStats> children) {
      Preconditions.checkArgument(
          !restart, "The supervisor strategy should never restart an actor.");

      akkaRpcActorFailed(child, cause);
    }
  }

  private static final class AkkaRpcActorRegistration {
    private final String endpointId;

    private final CompletableFuture<Void> internalTerminationFuture;

    private final CompletableFuture<Void> externalTerminationFuture;

    @Nullable private Throwable errorCause;

    private AkkaRpcActorRegistration(String endpointId) {
      this.endpointId = endpointId;
      internalTerminationFuture = new CompletableFuture<>();
      externalTerminationFuture = new CompletableFuture<>();
      errorCause = null;
    }

    private CompletableFuture<Void> getInternalTerminationFuture() {
      return internalTerminationFuture;
    }

    private CompletableFuture<Void> getExternalTerminationFuture() {
      return externalTerminationFuture;
    }

    private String getEndpointId() {
      return endpointId;
    }

    private void terminate(Executor terminationFutureExecutor) {
      CompletableFuture<Void> terminationFuture = internalTerminationFuture;

      if (errorCause != null) {
        if (!internalTerminationFuture.completeExceptionally(errorCause)) {
          // we have another failure reason -> let's add it
          terminationFuture =
              internalTerminationFuture.handle(
                  (ignored, throwable) -> {
                    if (throwable != null) {
                      errorCause.addSuppressed(throwable);
                    }

                    throw new CompletionException(errorCause);
                  });
        }
      } else {
        internalTerminationFuture.completeExceptionally(
            new RpcException(
                String.format(
                    "RpcEndpoint %s did not complete the internal termination future.",
                    endpointId)));
      }

      FutureUtil.forwardAsync(
          terminationFuture, externalTerminationFuture, terminationFutureExecutor);
    }

    private void terminateExceptionally(Throwable cause, Executor terminationFutureExecutor) {
      terminationFutureExecutor.execute(
          () -> externalTerminationFuture.completeExceptionally(cause));
    }

    public void markFailed(Throwable cause) {
      if (errorCause == null) {
        errorCause = cause;
      } else {
        errorCause.addSuppressed(cause);
      }
    }
  }

  // -----------------------------------------------------------------------------
  // Messages
  // -----------------------------------------------------------------------------

  static final class StartAkkaRpcActor {
    private final PropsFactory propsFactory;
    private final String endpointId;

    private StartAkkaRpcActor(PropsFactory propsFactory, String endpointId) {
      this.propsFactory = propsFactory;
      this.endpointId = endpointId;
    }

    public String getEndpointId() {
      return endpointId;
    }

    public PropsFactory getPropsFactory() {
      return propsFactory;
    }

    private static StartAkkaRpcActor create(PropsFactory propsFactory, String endpointId) {
      return new StartAkkaRpcActor(propsFactory, endpointId);
    }

    interface PropsFactory {
      Props create(CompletableFuture<Void> terminationFuture);
    }
  }

  static final class ActorRegistration {
    private final ActorRef actorRef;
    private final CompletableFuture<Void> terminationFuture;

    private ActorRegistration(ActorRef actorRef, CompletableFuture<Void> terminationFuture) {
      this.actorRef = actorRef;
      this.terminationFuture = terminationFuture;
    }

    public ActorRef getActorRef() {
      return actorRef;
    }

    public CompletableFuture<Void> getTerminationFuture() {
      return terminationFuture;
    }

    public static ActorRegistration create(
        ActorRef actorRef, CompletableFuture<Void> terminationFuture) {
      return new ActorRegistration(actorRef, terminationFuture);
    }
  }

  static final class StartAkkaRpcActorResponse {
    @Nullable private final ActorRegistration actorRegistration;

    @Nullable private final Throwable error;

    private StartAkkaRpcActorResponse(
        @Nullable ActorRegistration actorRegistration, @Nullable Throwable error) {
      this.actorRegistration = actorRegistration;
      this.error = error;
    }

    public <X extends Throwable> ActorRegistration orElseThrow(
        Function<? super Throwable, ? extends X> throwableFunction) throws X {
      if (actorRegistration != null) {
        return actorRegistration;
      } else {
        throw throwableFunction.apply(error);
      }
    }

    public static StartAkkaRpcActorResponse success(ActorRegistration actorRegistration) {
      return new StartAkkaRpcActorResponse(actorRegistration, null);
    }

    public static StartAkkaRpcActorResponse failure(Throwable error) {
      return new StartAkkaRpcActorResponse(null, error);
    }
  }
}

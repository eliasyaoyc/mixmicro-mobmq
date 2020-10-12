package xyz.vopen.framework.neptune.rpc.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;
import scala.concurrent.impl.Promise;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.common.utils.SerializedValue;
import xyz.vopen.framework.neptune.common.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.rpc.MainThreadValidatorUtil;
import xyz.vopen.framework.neptune.rpc.RpcEndpoint;
import xyz.vopen.framework.neptune.rpc.RpcGateway;
import xyz.vopen.framework.neptune.rpc.exceptions.*;
import xyz.vopen.framework.neptune.rpc.message.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AkkaRpcActor} Akka rpc actor which receives {@link LocalRpcInvocation}, {@link RunAsync}
 * and {@link CallAsync} {@link ControlMessages} messages.
 *
 * <p>The {@link LocalRpcInvocation} designates a rpc and is dispatched to the given {@link
 * RpcEndpoint} instance.
 *
 * <p>The {@link RunAsync} and {@link CallAsync} messages contain executable code which is executed
 * in the context of the actor tread.
 *
 * <p>The {@link ControlMessages} message controls the processing behaviour of the akka rpc actor. A
 * {@link ControlMessages#START} starts processing incoming messages. A {@link ControlMessages#STOP}
 * message stops processing messages. All messages with arrive when the processing is stopped, will
 * be discarded.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
class AkkaRpcActor<T extends RpcEndpoint & RpcGateway> extends AbstractActor {
  private static final Logger logger = LoggerFactory.getLogger(AkkaRpcActor.class);

  /** The endpoint to invoke the methods on. */
  protected final T rpcEndpoint;

  private final MainThreadValidatorUtil mainThreadValidator;

  private final CompletableFuture<Boolean> terminationFuture;

  private final int version;

  private final long maximumFrameSize;

  private final AtomicBoolean rpcEndpointStopped;

  private volatile RpcEndpointTerminationResult rpcEndpointTerminationResult;

  private @Nonnull State state;

  AkkaRpcActor(
      final T rpcEndpoint,
      final CompletableFuture<Boolean> terminationFuture,
      final int version,
      final long maximumFrameSize) {
    Preconditions.checkArgument(maximumFrameSize > 0, "Maximum frameSize must be positive.");
    this.rpcEndpoint = Preconditions.checkNotNull(rpcEndpoint, "rpc endpoint");
    this.mainThreadValidator = new MainThreadValidatorUtil(rpcEndpoint);
    this.terminationFuture = Preconditions.checkNotNull(terminationFuture);
    this.version = version;
    this.maximumFrameSize = maximumFrameSize;
    this.rpcEndpointStopped = new AtomicBoolean(false);
    this.rpcEndpointTerminationResult =
        RpcEndpointTerminationResult.failure(
            new RpcException(
                String.format(
                    "RpcEndpoint %s has not been properly stopped.", rpcEndpoint.getEndpointId())));
    this.state = StoppedState.STOPPED;
  }

  @Override
  public void postStop() throws Exception {
    super.postStop();

    if (rpcEndpointTerminationResult.isSuccess()) {
      terminationFuture.complete(null);
    } else {
      terminationFuture.completeExceptionally(rpcEndpointTerminationResult.getFailureCause());
    }

    state = state.finishTermination();
  }

  @Override
  public Receive createReceive() {
    return ReceiveBuilder.create()
        .match(RemoteHandshakeMessage.class, this::handleHandshakeMessage)
        .match(ControlMessages.class, this::handleControlMessage)
        .matchAny(this::handleMessage)
        .build();
  }

  private void handleMessage(final Object message) {
    if (state.isRunning()) {
      mainThreadValidator.enterMainThread();

      try {
        handleRpcMessage(message);
      } finally {
        mainThreadValidator.exitMainThread();
      }
    } else {
      logger.info(
          "The rpc endpoint {} has not been started yet. Discarding message {} until processing is started.",
          rpcEndpoint.getClass().getName(),
          message.getClass().getName());
      sendErrorIfSender(
          new RpcException(
              String.format(
                  "Discard message, because the rpc endpoint %s has not been started yet.",
                  rpcEndpoint.getAddress())));
    }
  }

  private void handleControlMessage(ControlMessages controlMessages) {
    try {
      switch (controlMessages) {
        case START:
          state = state.start(this);
          break;
        case STOP:
          state = state.stop();
          break;
        case TERMINATE:
          state = state.terminate(this);
          break;
        default:
          handleUnknownControlMessage(controlMessages);
      }
    } catch (Exception e) {
      this.rpcEndpointTerminationResult = RpcEndpointTerminationResult.failure(e);
      throw e;
    }
  }

  private void handleUnknownControlMessage(ControlMessages controlMessages) {
    final String message =
        String.format(
            "Received unknown control message %s. Dropping this message!", controlMessages);
    logger.warn(message);
    sendErrorIfSender(new AkkaUnknownMessageException(message));
  }

  protected void handleRpcMessage(Object message) {
    if (message instanceof RunAsync) {
      handleRunAsync((RunAsync) message);
    } else if (message instanceof CallAsync) {
      handleCallAsync((CallAsync) message);
    } else if (message instanceof RpcInvocation) {
      handleRpcInvocation((RpcInvocation) message);
    } else {
      logger.warn(
          "Received message of unknown type {} with value {}. Dropping this message!",
          message.getClass().getName(),
          message);

      sendErrorIfSender(
          new AkkaUnknownMessageException(
              "Received unknown message "
                  + message
                  + " of type "
                  + message.getClass().getSimpleName()
                  + '.'));
    }
  }

  private void handleHandshakeMessage(RemoteHandshakeMessage handshakeMessage) {
    if (!isCompatibleVersion(handshakeMessage.getVersion())) {
      sendErrorIfSender(
          new HandshakeException(
              String.format(
                  "Version mismatch between source (%s) and target (%s) rpc component. Please verify that all components have the same version.",
                  handshakeMessage.getVersion(), getVersion())));
    } else if (!isGatewaySupported(handshakeMessage.getRpcGateway())) {
      sendErrorIfSender(
          new HandshakeException(
              String.format(
                  "The rpc endpoint does not support the gateway %s.",
                  handshakeMessage.getRpcGateway().getSimpleName())));
    } else {
      getSender().tell(new Status.Success(HandshakeSuccessMessage.INSTANCE), getSelf());
    }
  }

  private boolean isGatewaySupported(Class<?> rpcGateway) {
    return rpcGateway.isAssignableFrom(rpcEndpoint.getClass());
  }

  private boolean isCompatibleVersion(int sourceVersion) {
    return sourceVersion == getVersion();
  }

  private int getVersion() {
    return this.version;
  }

  /**
   * Handle rpc invocations by looking up the rpc method on the rpc endpoint and calling this method
   * with the provided method arguments, if the method has a return value, it is returned to the
   * sender of the call.
   *
   * @param rpcInvocation Rpc invocation message.
   */
  private void handleRpcInvocation(RpcInvocation rpcInvocation) {
    Method rpcMethod = null;

    try {
      String methodName = rpcInvocation.getMethodName();
      Class<?>[] parameterTypes = rpcInvocation.getParameterTypes();

      // get method that needed to invoke.
      rpcMethod = lookupRpcMethod(methodName, parameterTypes);
    } catch (ClassNotFoundException e) {
      logger.error("Could not load method arguments.", e);
      RpcConnectionException rpcException =
          new RpcConnectionException("Could not load method arguments.", e);
      getSender().tell(new Status.Failure(rpcException), getSelf());
    } catch (IOException e) {
      logger.error("Could not deserialize rpc invocation message.", e);

      RpcConnectionException rpcException =
          new RpcConnectionException("Could not deserialize rpc invocation message.", e);
      getSender().tell(new Status.Failure(rpcException), getSelf());
    } catch (final NoSuchMethodException e) {
      logger.error("Could not find rpc method for rpc invocation.", e);

      RpcConnectionException rpcException =
          new RpcConnectionException("Could not find rpc method for rpc invocation.", e);
      getSender().tell(new Status.Failure(rpcException), getSelf());
    }

    // 通过反射执行
    if (rpcMethod != null) {
      try {
        // this supports declaration of anonymous classes
        rpcMethod.setAccessible(true);

        if (rpcMethod.getReturnType().equals(Void.TYPE)) {
          // No return value to send back
          rpcMethod.invoke(rpcEndpoint, rpcInvocation.getArgs());
        } else {
          final Object result;
          try {
            result = rpcMethod.invoke(rpcEndpoint, rpcInvocation.getArgs());
          } catch (InvocationTargetException e) {
            logger.debug("Reporting back error thrown in remote procedure {}", rpcMethod, e);

            // tell the sender about the failure
            getSender().tell(new Status.Failure(e.getTargetException()), getSelf());
            return;
          }

          final String methodName = rpcMethod.getName();

          // send result to caller.
          if (result instanceof CompletableFuture) {
            final CompletableFuture<?> responseFuture = (CompletableFuture<?>) result;
            sendAsyncResponse(responseFuture, methodName);
          } else {
            sendSyncResponse(result, methodName);
          }
        }
      } catch (Throwable e) {
        logger.error("Error while executing remote procedure call {}.", rpcMethod, e);
        // tell the sender about the failure
        getSender().tell(new Status.Failure(e), getSelf());
      }
    }
  }

  private void sendSyncResponse(Object response, String methodName) {
    if (isRemoteSender(getSender())) {
      Either<SerializedValue<?>, RpcException> serializedResult =
          serializeRemoteResultAndVerifySize(response, methodName);

      if (serializedResult.isLeft()) {
        getSender().tell(new Status.Success(serializedResult.left()), getSelf());
      } else {
        getSender().tell(new Status.Failure(serializedResult.right().get()), getSelf());
      }
    } else {
      getSender().tell(new Status.Success(response), getSelf());
    }
  }

  private void sendAsyncResponse(CompletableFuture<?> asyncResponse, String methodName) {
    final ActorRef sender = getSender();
    Promise.DefaultPromise<Object> promise = new Promise.DefaultPromise<>();

    asyncResponse.whenComplete(
        (value, throwable) -> {
          if (throwable != null) {
            promise.failure(throwable);
          } else {
            if (isRemoteSender(sender)) {
              Either<SerializedValue<?>, RpcException> serializedResult =
                  serializeRemoteResultAndVerifySize(value, methodName);

              if (serializedResult.isLeft()) {
                promise.success(serializedResult.left());
              } else {
                promise.failure(serializedResult.right().get());
              }
            } else {
              promise.success(value);
            }
          }
        });

    Patterns.pipe(promise.future(), getContext().dispatcher()).to(sender);
  }

  private boolean isRemoteSender(ActorRef sender) {
    return !sender.path().address().hasLocalScope();
  }

  private Either<SerializedValue<?>, RpcException> serializeRemoteResultAndVerifySize(
      Object result, String methodName) {
    try {
      SerializedValue<?> serializedResult = new SerializedValue<>(result);

      long resultSize = serializedResult.getByteArray().length;
      if (resultSize > maximumFrameSize) {
        return new Right(
            new RpcException(
                "The method "
                    + methodName
                    + "'s result size "
                    + resultSize
                    + " exceeds the maximum size "
                    + maximumFrameSize
                    + " ."));
      } else {
        return new Left(serializedResult);
      }
    } catch (IOException e) {
      return new Right(
          new RpcException("Failed to serialize the result for RPC call : " + methodName + ".", e));
    }
  }

  /**
   * Handle asynchronous {@link java.util.concurrent.Callable  . This method simply execites the given {@link java.util.concurrent.Callable}
   * in the context of the actor thread.
   *
   * @param callAsync Call async message.
   */
  private void handleCallAsync(CallAsync callAsync) {
    try {
      Object result = callAsync.getCallable().call();

      getSender().tell(new Status.Success(result), getSelf());
    } catch (Throwable e) {
      getSender().tell(new Status.Failure(e), getSelf());
    }
  }

  /**
   * Handle asynchronous {@link Runnable}. This method simply executes the given {@link Runnable} in
   * the context of the actor thread.
   *
   * @param runAsync Run async message
   */
  private void handleRunAsync(RunAsync runAsync) {
    final long timeToRun = runAsync.getTimeNanos();
    final long delayNanos;

    if (timeToRun == 0 || (delayNanos = timeToRun - System.nanoTime()) <= 0) {
      // run immediately
      try {
        runAsync.getRunnable().run();
      } catch (Throwable t) {
        logger.error("Caught exception while executing runnable in main thread.", t);
        ExceptionUtil.rethrowIfFatalErrorOrOOM(t);
      }
    } else {
      // schedule for later. send a new message after the delay, which will then be immediately
      // executed
      FiniteDuration delay = new FiniteDuration(delayNanos, TimeUnit.NANOSECONDS);
      RunAsync message = new RunAsync(runAsync.getRunnable(), timeToRun);

      final Object envelopedSelfMessage = envelopeSelfMessage(message);

      getContext()
          .system()
          .scheduler()
          .scheduleOnce(
              delay,
              getSelf(),
              envelopedSelfMessage,
              getContext().dispatcher(),
              ActorRef.noSender());
    }
  }

  /**
   * Look up the rpc method on the given {@link RpcEndpoint} instance.
   *
   * @param methodName Name of the method.
   * @param parameterTypes Parameter types of the method.
   * @return Method of the rpc endpoint.
   * @throws NoSuchMethodException Thrown if the method with the given name and parameter types
   *     cannot be found at the rpc endpoint.
   */
  private Method lookupRpcMethod(final String methodName, final Class<?>[] parameterTypes)
      throws NoSuchMethodException {
    return this.rpcEndpoint.getClass().getMethod(methodName, parameterTypes);
  }

  /**
   * Send throwable to sender if the sender is specified.
   *
   * @param throwable to send to the sender
   */
  protected void sendErrorIfSender(Throwable throwable) {
    if (!getSender().equals(ActorRef.noSender())) {
      getSender().tell(new Status.Failure(throwable), getSelf());
    }
  }

  /**
   * Hook to envelope self messages.
   *
   * @param message to envelope
   * @return enveloped message
   */
  protected Object envelopeSelfMessage(Object message) {
    return message;
  }

  /**
   * Stop the actor immediately.
   *
   * @param rpcEndpointTerminationResult
   */
  private void stop(RpcEndpointTerminationResult rpcEndpointTerminationResult) {
    if (rpcEndpointStopped.compareAndSet(false, true)) {
      this.rpcEndpointTerminationResult = rpcEndpointTerminationResult;
      getContext().stop(getSelf());
    }
  }

  // ----------------------------------------------------------------------------
  // Internal state machine
  // ---------------------------------------------------------------------------

  interface State {
    default State start(AkkaRpcActor<?> akkaRpcActor) {
      throw new AkkaRpcInvalidStateException(invalidStateTransitionMessage(StartedState.STARTED));
    }

    default State stop() {
      throw new AkkaRpcInvalidStateException(invalidStateTransitionMessage(StoppedState.STOPPED));
    }

    default State terminate(AkkaRpcActor<?> akkaRpcActor) {
      throw new AkkaRpcInvalidStateException(
          invalidStateTransitionMessage(TerminatingState.TERMINATING));
    }

    default State finishTermination() {
      return TerminatedState.TERMINATED;
    }

    default boolean isRunning() {
      return false;
    }

    default String invalidStateTransitionMessage(State targetState) {
      return String.format(
          "AkkaRpcActor is currently in state %s and cannot go into state %s.", this, targetState);
    }
  }

  @SuppressWarnings("Singleton")
  enum StartedState implements State {
    STARTED;

    @Override
    public State start(AkkaRpcActor<?> akkaRpcActor) {
      return STARTED;
    }

    @Override
    public State stop() {
      return StoppedState.STOPPED;
    }

    @Override
    public State terminate(AkkaRpcActor<?> akkaRpcActor) {
      akkaRpcActor.mainThreadValidator.enterMainThread();

      CompletableFuture<Void> terminationFuture;
      try {
        terminationFuture = akkaRpcActor.rpcEndpoint.internalCallOnStop();
      } catch (Throwable t) {
        terminationFuture =
            FutureUtil.completedExceptionally(
                new RpcException(
                    String.format(
                        "Failure while stopping RpcEndpoint %s.",
                        akkaRpcActor.rpcEndpoint.getEndpointId()),
                    t));
      } finally {
        akkaRpcActor.mainThreadValidator.exitMainThread();
      }

      // IMPORTANT: This only works if we don't use a restarting supervisor strategy. Otherwise
      // we would complete the future and let the actor system restart the actor with a completed
      // future.
      // Complete the termination future so that others know that we've stopped.

      terminationFuture.whenComplete(
          (ignored, throwable) -> akkaRpcActor.stop(RpcEndpointTerminationResult.of(throwable)));

      return TerminatingState.TERMINATING;
    }

    @Override
    public boolean isRunning() {
      return true;
    }
  }

  @SuppressWarnings("Singleton")
  enum StoppedState implements State {
    STOPPED;

    @Override
    public State start(AkkaRpcActor<?> akkaRpcActor) {
      akkaRpcActor.mainThreadValidator.enterMainThread();

      try {
        akkaRpcActor.rpcEndpoint.internalCallOnStart();
      } catch (Throwable throwable) {
        akkaRpcActor.stop(
            RpcEndpointTerminationResult.failure(
                new RpcException(
                    String.format(
                        "Could not start RpcEndpoint %s.",
                        akkaRpcActor.rpcEndpoint.getEndpointId()),
                    throwable)));
      } finally {
        akkaRpcActor.mainThreadValidator.exitMainThread();
      }

      return StartedState.STARTED;
    }

    @Override
    public State stop() {
      return STOPPED;
    }

    @Override
    public State terminate(AkkaRpcActor<?> akkaRpcActor) {
      akkaRpcActor.stop(RpcEndpointTerminationResult.success());

      return TerminatingState.TERMINATING;
    }
  }

  @SuppressWarnings("Singleton")
  enum TerminatingState implements State {
    TERMINATING;

    @Override
    public State terminate(AkkaRpcActor<?> akkaRpcActor) {
      return TERMINATING;
    }

    @Override
    public boolean isRunning() {
      return true;
    }
  }

  enum TerminatedState implements State {
    TERMINATED
  }

  private static final class RpcEndpointTerminationResult {
    private static final RpcEndpointTerminationResult SUCCESS =
        new RpcEndpointTerminationResult(null);

    private final @Nullable Throwable failureCause;

    private RpcEndpointTerminationResult(@Nullable Throwable failureCause) {
      this.failureCause = failureCause;
    }

    public boolean isSuccess() {
      return failureCause == null;
    }

    public Throwable getFailureCause() {
      Preconditions.checkState(failureCause != null);
      return failureCause;
    }

    private static RpcEndpointTerminationResult success() {
      return SUCCESS;
    }

    private static RpcEndpointTerminationResult failure(Throwable failureCause) {
      return new RpcEndpointTerminationResult(failureCause);
    }

    private static RpcEndpointTerminationResult of(@Nullable Throwable failureCause) {
      if (failureCause == null) {
        return success();
      } else {
        return failure(failureCause);
      }
    }
  }
}

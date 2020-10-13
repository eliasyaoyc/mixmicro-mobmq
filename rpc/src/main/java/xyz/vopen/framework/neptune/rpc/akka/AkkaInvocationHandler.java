package xyz.vopen.framework.neptune.rpc.akka;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneRuntimeException;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.common.utils.SerializedValue;
import xyz.vopen.framework.neptune.common.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.rpc.exceptions.RpcException;
import xyz.vopen.framework.neptune.rpc.message.*;
import xyz.vopen.framework.neptune.rpc.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * {@link AkkaInvocationHandler} Invocation handler to be used with a {@link AkkaRpcActor}. The
 * invocation handler wraps the rpc in a {@link
 * xyz.vopen.framework.neptune.rpc.message.LocalRpcInvocation} message and then sends it to the
 * {@link AkkaRpcActor} where is it executed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaInvocationHandler implements InvocationHandler, AkkaBasedEndpoint, RpcServer {
  private static final Logger logger = LoggerFactory.getLogger(AkkaInvocationHandler.class);

  /**
   * The Akka (RPC) address of {@link #rpcEndpoint} including host and port of the ActorSystem im
   * which the actor is running.
   */
  private final String address;

  /** Hostname of the host, {@link #rpcEndpoint} is running on. */
  private final String hostname;

  private final ActorRef rpcEndpoint;

  /** Whether the actor ref is local and thus no message serialization i needed. */
  protected final boolean isLocal;

  /** Default timeout for asks. */
  private final Time timeout;

  private final long maximumFrameSize;

  /** Null if gateway; otherwise non-null. */
  private final @Nonnull CompletableFuture<Void> terminationFuture;

  private final boolean captureAskCallStack;

  AkkaInvocationHandler(
      String address,
      String hostname,
      ActorRef rpcEndpoint,
      Time timeout,
      long maximumFrameSize,
      @Nullable CompletableFuture<Void> terminationFuture,
      boolean captureAskCallStack) {
    this.address = address;
    this.hostname = hostname;
    this.rpcEndpoint = rpcEndpoint;
    this.isLocal = this.rpcEndpoint.path().address().hasLocalScope();
    this.timeout = timeout;
    this.maximumFrameSize = maximumFrameSize;
    this.terminationFuture = terminationFuture;
    this.captureAskCallStack = captureAskCallStack;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class<?> declaringClass = method.getDeclaringClass();

    Object result;
    if (declaringClass.equals(AkkaBasedEndpoint.class)
        || declaringClass.equals(Object.class)
        || declaringClass.equals(RpcGateway.class)
        || declaringClass.equals(RpcStartStoppable.class)
        || declaringClass.equals(MainThreadExecutable.class)
        || declaringClass.equals(RpcServer.class)) {
      result = method.invoke(this, args);
    } else {
      result = invokeRpc(method, args);
    }
    return result;
  }

  @Override
  public ActorRef getActorRef() {
    return this.rpcEndpoint;
  }

  @Override
  public CompletableFuture<Void> getTerminationFuture() {
    return this.terminationFuture;
  }

  @Override
  public void runAsync(Runnable runnable) {
    scheduleRunAsync(runnable, 0L);
  }

  @Override
  public void scheduleRunAsync(Runnable runnable, long delay) {
    Preconditions.checkNotNull(runnable, "runnable");
    Preconditions.checkArgument(delay >= 0, "delay must be zero or greater");

    if (isLocal) {
      long atTimeNanos = delay == 0 ? 0 : System.nanoTime() + (delay * 1_000_000);
      tell(new RunAsync(runnable, atTimeNanos));
    } else {
      throw new NeptuneRuntimeException(
          "Trying to send a Runnable to a remote actor at "
              + rpcEndpoint.path()
              + ". This is not supported.");
    }
  }

  @Override
  public <V> CompletableFuture<V> callAsync(Callable<V> callable, Time callTimeout) {
    if (isLocal) {
      CompletableFuture<V> resultFuture =
          (CompletableFuture<V>) ask(new CallAsync(callable), callTimeout);
      return resultFuture;
    } else {
      throw new NeptuneRuntimeException(
          "Trying to send a Callable to a remote actor at "
              + rpcEndpoint.path()
              + ". This is not supported.");
    }
  }

  @Override
  public void start() {
    this.rpcEndpoint.tell(ControlMessages.START, ActorRef.noSender());
  }

  @Override
  public void stop() {
    this.rpcEndpoint.tell(ControlMessages.STOP, ActorRef.noSender());
  }

  @Override
  public String getAddress() {
    return this.address;
  }

  @Override
  public String getHostname() {
    return this.hostname;
  }

  /**
   * Invokes a RPC method by sending the RPC invocation details to the rpc endpoint.
   *
   * @param method to call.
   * @param args of the method call.
   * @return result of the RPC.
   * @throws Exception if the RPC invocation fails.
   */
  private Object invokeRpc(Method method, Object[] args) throws Exception {
    String methodName = method.getName();
    Class<?>[] parameterTypes = method.getParameterTypes();
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    Time futureTimeout = extractRpcTimeout(parameterAnnotations, args, timeout);

    // Encapsulate the rpc call as RpcInvocation (depending on whether RpcEndpoint is local or
    // remote).
    final RpcInvocation rpcInvocation =
        createRpcInvocationMessage(methodName, parameterTypes, args);

    // Decide whether to call tell or ask based on whether the RPC method has a return value.
    Class<?> returnType = method.getReturnType();

    final Object result;

    //
    if (Objects.equals(returnType, Void.TYPE)) {
      tell(rpcInvocation);
      result = null;
    } else {
      // Capture the call stack, It is significantly faster to do that via an exception than via
      // Thread.getStackTrace(), because exceptions
      // lazily initialize the stack trace, initially only capture a lightweight native pointer, and
      // convert that into the stack trace lazily when needed.
      final Throwable callStackCapture = captureAskCallStack ? new Throwable() : null;

      // execute an asynchronous call.
      final CompletableFuture<?> resultFuture = ask(rpcInvocation, futureTimeout);

      final CompletableFuture<Object> completableFuture = new CompletableFuture<>();
      resultFuture.whenComplete(
          (resultValue, failure) -> {
            if (failure != null) {
              completableFuture.completeExceptionally(
                  resolveTimeoutException(failure, callStackCapture, method));
            } else {
              completableFuture.complete(deserializeValueIfNeeded(resultValue, method));
            }
          });

      if (Objects.equals(returnType, CompletableFuture.class)) {
        result = completableFuture;
      } else {
        try {
          result = completableFuture.get(futureTimeout.getSize(), futureTimeout.getUnit());
        } catch (ExecutionException e) {
          throw new RpcException(
              "Failure while obtaining synchronous RPC result.",
              ExceptionUtil.stripExecutionException(e));
        }
      }
    }
    return result;
  }

  /**
   * Create the RpcInvocation message for the given RPC.
   *
   * @param methodName of the RPC.
   * @param parameterTypes of the RPC.
   * @param args of the RPC.
   * @return RpcInvocation message which encapsulates the RPC details.
   * @throws IOException
   */
  protected RpcInvocation createRpcInvocationMessage(
      final String methodName, final Class<?>[] parameterTypes, final Object[] args)
      throws IOException {
    final RpcInvocation rpcInvocation;

    if (isLocal) {
      rpcInvocation = new LocalRpcInvocation(methodName, parameterTypes, args);
    } else {
      try {
        RemoteRpcInvocation remoteRpcInvocation =
            new RemoteRpcInvocation(methodName, parameterTypes, args);

        if (remoteRpcInvocation.getSize() > maximumFrameSize) {
          throw new IOException(
              String.format(
                  "The rpc invocation size %d exceeds the maximum akka frameSize.",
                  remoteRpcInvocation.getSize()));
        } else {
          rpcInvocation = remoteRpcInvocation;
        }
      } catch (IOException e) {
        logger.warn(
            "Could not create remote rpc invocation message. Failing rpc invocation because...");
        throw e;
      }
    }
    return rpcInvocation;
  }

  /**
   * Extracts the {@link RpcTimeout} annotated rpc timeout value from the list of given method
   * arguments. If no {@link RpcTimeout} annotated parameter could be found, then the default
   * timeout is returned.
   *
   * @param parameterAnnotations Parameter annotations.
   * @param args Array of arguments.
   * @param defaultTimeout Default timeout to return if no {@link RpcTimeout} annotated parameter
   *     has been found.
   * @return Timeout extracted from the array of arguments or the default timeout.
   */
  private static Time extractRpcTimeout(
      Annotation[][] parameterAnnotations, Object[] args, Time defaultTimeout) {
    if (args != null) {
      Preconditions.checkArgument(parameterAnnotations.length == args.length);

      for (int i = 0; i < parameterAnnotations.length; i++) {
        if (isRpcTimeout(parameterAnnotations[i])) {
          if (args[i] instanceof Time) {
            return (Time) args[i];
          } else {
            throw new NeptuneRuntimeException(
                "The rpc timeout parameter must be of type "
                    + Time.class.getName()
                    + ". The type "
                    + args[i].getClass().getName()
                    + " is not supported.");
          }
        }
      }
    }
    return defaultTimeout;
  }

  /**
   * Checks whether any of the annotations is of type {@link RpcTimeout}.
   *
   * @param annotations Array of annotations
   * @return True if {@link RpcTimeout} was found; otherwise false
   */
  private static boolean isRpcTimeout(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(RpcTimeout.class)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Sends the message to the RPC endpoint.
   *
   * @param message to send to the RPC endpoint.
   */
  protected void tell(Object message) {
    rpcEndpoint.tell(message, ActorRef.noSender());
  }

  /**
   * Sends the message to the RPC endpoint and returns a future containing its response.
   *
   * @param message to send to the RPC endpoint
   * @param timeout time to wait until the response future is failed with a {@link TimeoutException}
   * @return Response future
   */
  protected CompletableFuture<?> ask(Object message, Time timeout) {
    return FutureUtil.toJava(Patterns.ask(rpcEndpoint, message, timeout.toMilliseconds()));
  }

  static Object deserializeValueIfNeeded(Object o, Method method) {
    if (o instanceof SerializedValue) {
      try {
        return ((SerializedValue<?>) o)
            .deserializeValue(AkkaInvocationHandler.class.getClassLoader());
      } catch (IOException | ClassNotFoundException e) {
        throw new CompletionException(
            new RpcException(
                "Could not deserialize the serialized payload of RPC method : " + method.getName(),
                e));
      }
    } else {
      return o;
    }
  }

  static Throwable resolveTimeoutException(
      Throwable exception, @Nullable Throwable callStackCapture, Method method) {
    if (!(exception instanceof akka.pattern.AskTimeoutException)) {
      return exception;
    }

    final TimeoutException newException =
        new TimeoutException("Invocation of " + method + " timed out.");
    newException.initCause(exception);

    if (callStackCapture != null) {
      // remove the stack frames coming from the proxy interface invocation
      final StackTraceElement[] stackTrace = callStackCapture.getStackTrace();
      newException.setStackTrace(Arrays.copyOfRange(stackTrace, 3, stackTrace.length));
    }

    return newException;
  }
}

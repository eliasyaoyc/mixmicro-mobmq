package xyz.vopen.framework.neptune.core.dispatcher.runner;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.annoations.Internal;
import xyz.vopen.framework.neptune.common.annoations.VisibleForTesting;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.utils.AutoCloseableAsync;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.core.dispatcher.DispatcherGateway;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.core.web.RestfulGateway;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link AbstractDispatcherLeaderProcess} Basic implementation.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
@Internal
public abstract class AbstractDispatcherLeaderProcess implements DispatcherLeaderProcess {
  private static final Logger logger =
      LoggerFactory.getLogger(AbstractDispatcherLeaderProcess.class);

  private final UUID leaderSessionId;

  private final FatalErrorHandler fatalErrorHandler;

  private CompletableFuture<DispatcherGateway> dispatcherGatewayFuture;

  private final CompletableFuture<String> leaderAddressFuture;

  private final CompletableFuture<Void> terminationFuture;

  private final CompletableFuture<ApplicationStatus> shutDownFuture;

  private State state;
  private @Nullable DispatcherGatewayService dispatcherGatewayService;

  AbstractDispatcherLeaderProcess(UUID leaderSessionId, FatalErrorHandler fatalErrorHandler) {
    this.leaderSessionId = leaderSessionId;
    this.fatalErrorHandler = fatalErrorHandler;

    this.dispatcherGatewayFuture = new CompletableFuture<>();
    this.leaderAddressFuture = dispatcherGatewayFuture.thenApply(RestfulGateway::getAddress);
    this.terminationFuture = new CompletableFuture<>();
    this.shutDownFuture = new CompletableFuture<>();

    this.state = State.CREATED;
  }

  @VisibleForTesting
  @GuardedBy("lock")
  State getState() {
    return state;
  }

  @Override
  public void start() {
    runIfStateIs(State.CREATED, this::startInternal);
  }

  private void startInternal() {
    logger.info("Start {}.", getClass().getSimpleName());
    state = State.RUNNING;
    onStart();
  }

  @Override
  public UUID getLeaderSessionId() {
    return this.leaderSessionId;
  }

  @Override
  public CompletableFuture<DispatcherGateway> getDispatcherGateway() {
    return this.dispatcherGatewayFuture;
  }

  @Override
  public CompletableFuture<String> getLeaderAddressFuture() {
    return this.leaderAddressFuture;
  }

  @Override
  public CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return this.shutDownFuture;
  }

  protected final Optional<DispatcherGatewayService> getDispatcherService() {
    return Optional.ofNullable(this.dispatcherGatewayService);
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    runIfStateIsNot(State.STOPPED, this::closeInternal);
    return terminationFuture;
  }

  private void closeInternal() {
    logger.info("Stopping {}.", getClass().getSimpleName());

    final CompletableFuture<Void> dispatcherServiceTerminationFuture = closeDispatcherService();

    final CompletableFuture<Void> onCloseTerminationFuture =
        FutureUtil.composeAfterwards(dispatcherServiceTerminationFuture, this::onClose);

    FutureUtil.forward(onCloseTerminationFuture, this.terminationFuture);

    state = State.STOPPED;
  }

  private CompletableFuture<Void> closeDispatcherService() {
    if (dispatcherGatewayService != null) {
      return dispatcherGatewayService.closeAsync();
    } else {
      return FutureUtil.completedVoidFuture();
    }
  }

  protected abstract void onStart();

  protected CompletableFuture<Void> onClose() {
    return FutureUtil.completedVoidFuture();
  }

  final void completeDispatcherSetup(DispatcherGatewayService dispatcherGatewayService) {
    runIfStateIs(State.RUNNING, () -> completeDispatcherSetupInternal(dispatcherGatewayService));
  }

  private void completeDispatcherSetupInternal(DispatcherGatewayService createDispatcherService) {
    Preconditions.checkState(
        dispatcherGatewayService == null, "The DispatcherGatewayService can only be set once.");
    dispatcherGatewayFuture = (CompletableFuture<DispatcherGateway>) createDispatcherService;
    dispatcherGatewayFuture.complete(createDispatcherService.getGateway());
    FutureUtil.forward(createDispatcherService.getShutDownFuture(), shutDownFuture);
  }

  @GuardedBy("lock")
  final <V> Optional<V> supplyUnsynchronizedIfRunning(Supplier<V> supplier) {
    if (state != State.RUNNING) {
      return Optional.empty();
    }
    return Optional.of(supplier.get());
  }

  @GuardedBy("lock")
  final <V> Optional<V> supplyIfRunning(Supplier<V> supplier) {
    if (state != State.RUNNING) {
      return Optional.empty();
    }
    return Optional.of(supplier.get());
  }

  final void runIfStateIs(State expectedState, Runnable action) {
    runIfState(expectedState::equals, action);
  }

  private void runIfStateIsNot(State notExpectedState, Runnable action) {
    runIfState(state -> !notExpectedState.equals(state), action);
  }

  @GuardedBy("lock")
  private void runIfState(Predicate<State> actionPredicate, Runnable action) {
    if (actionPredicate.test(state)) {
      action.run();
    }
  }

  @GuardedBy("lock")
  final <T> Void onErrorIfRunning(T ignored, Throwable throwable) {
    if (state != State.RUNNING) {
      return null;
    }

    if (throwable != null) {
      closeAsync();
      fatalErrorHandler.onFatalError(throwable);
    }
    return null;
  }

  /** The state of the {@link DispatcherLeaderProcess}. */
  protected enum State {
    CREATED,
    RUNNING,
    STOPPED
  }

  // ===================== Internal classes =====================

  /** Factory for {@link DispatcherGatewayService}. */
  public interface DispatcherGatewayServiceFactory {
    DispatcherGatewayService create();
  }

  /** An accessor of the {@link DispatcherGateway}. */
  public interface DispatcherGatewayService extends AutoCloseableAsync {

    DispatcherGateway getGateway();

    CompletableFuture<Void> onRemovedJob(String jobId);

    CompletableFuture<ApplicationStatus> getShutDownFuture();
  }
}

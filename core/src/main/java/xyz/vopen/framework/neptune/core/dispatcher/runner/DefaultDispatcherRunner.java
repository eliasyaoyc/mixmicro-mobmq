package xyz.vopen.framework.neptune.core.dispatcher.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;
import xyz.vopen.framework.neptune.core.dispatcher.DispatcherRunnerLeaderElectionLifecycleManager;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderContender;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderElectionService;
import xyz.vopen.framework.neptune.core.rpc.FatalErrorHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DefaultDispatcherRunner} Runner for the {@link
 * xyz.vopen.framework.neptune.core.dispatcher.Dispatcher} which is responsible for the leader
 * election.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DefaultDispatcherRunner implements DispatcherRunner, LeaderContender {
  private static final Logger logger = LoggerFactory.getLogger(DefaultDispatcherRunner.class);

  private final LeaderElectionService electionService;
  private final FatalErrorHandler fatalErrorHandler;
  private final DispatcherLeaderProcessFactory dispatcherLeaderProcessFactory;

  private DefaultDispatcherRunner(
      LeaderElectionService electionService,
      FatalErrorHandler fatalErrorHandler,
      DispatcherLeaderProcessFactory dispatcherLeaderProcessFactory) {
    this.electionService = electionService;
    this.fatalErrorHandler = fatalErrorHandler;
    this.dispatcherLeaderProcessFactory = dispatcherLeaderProcessFactory;
  }

  public static DispatcherRunner create(
      LeaderElectionService electionService,
      FatalErrorHandler fatalErrorHandler,
      DispatcherLeaderProcessFactory dispatcherLeaderProcessFactory)
      throws Exception {
    DefaultDispatcherRunner defaultDispatcherRunner =
        new DefaultDispatcherRunner(
            electionService, fatalErrorHandler, dispatcherLeaderProcessFactory);
    return DispatcherRunnerLeaderElectionLifecycleManager.createFor(
        defaultDispatcherRunner, electionService);
  }

  @Override
  public CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return null;
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    return null;
  }

  @Override
  public void grantLeadership(UUID leaderSessionID) {}

  @Override
  public void revokeLeadership() {}

  @Override
  public void handleError(Exception exception) {
    fatalErrorHandler.onFatalError(
        new NeptuneException(
            String.format(
                "Exception during leader exception of %s occurred.", getClass().getSimpleName()),
            exception));
  }
}

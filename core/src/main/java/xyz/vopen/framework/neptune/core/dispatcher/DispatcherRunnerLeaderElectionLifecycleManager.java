package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.enums.ApplicationStatus;
import xyz.vopen.framework.neptune.core.concurrent.FutureUtil;
import xyz.vopen.framework.neptune.core.dispatcher.runner.DispatcherRunner;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderContender;
import xyz.vopen.framework.neptune.core.leaderelection.LeaderElectionService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * {@link DispatcherRunnerLeaderElectionLifecycleManager}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class DispatcherRunnerLeaderElectionLifecycleManager<
        T extends DispatcherRunner & LeaderContender>
    implements DispatcherRunner {
  private final T dispatcherRunner;
  private final LeaderElectionService leaderElectionService;

  private DispatcherRunnerLeaderElectionLifecycleManager(
      T dispatcherRunner, LeaderElectionService leaderElectionService) throws Exception {
    this.dispatcherRunner = dispatcherRunner;
    this.leaderElectionService = leaderElectionService;

    // start dispatcher.
    leaderElectionService.start(dispatcherRunner);
  }

  public static <T extends DispatcherRunner & LeaderContender> DispatcherRunner createFor(
      T dispatcherRunner, LeaderElectionService leaderElectionService) throws Exception {
    return new DispatcherRunnerLeaderElectionLifecycleManager<>(
        dispatcherRunner, leaderElectionService);
  }

  @Override
  public CompletableFuture<ApplicationStatus> getShutDownFuture() {
    return dispatcherRunner.getShutDownFuture();
  }

  @Override
  public CompletableFuture<Void> closeAsync() {
    CompletableFuture<Void> servicesTerminationFuture = stopServices();
    CompletableFuture<Void> dispatcherRunnerTerminationFuture = dispatcherRunner.closeAsync();

    return FutureUtil.completeAll(
        Arrays.asList(servicesTerminationFuture, dispatcherRunnerTerminationFuture));
  }

  private CompletableFuture<Void> stopServices() {
    try {
      leaderElectionService.stop();
    } catch (Exception e) {
      return FutureUtil.completedExceptionally(e);
    }
    return FutureUtil.completedVoidFuture();
  }
}

package xyz.vopen.framework.neptune.core.leaderelection;

import java.util.UUID;

/**
 * {@link LeaderContender} Interface which has to be implemented to take part in the leader election
 * process if the {@link LeaderElectionService}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface LeaderContender {

  /**
   * Callback method which is called by the {@link LeaderElectionService} upon selecting this
   * instance as the new leader. The method is called with the new leader session ID.
   *
   * @param leaderSessionID New leader session ID.
   */
  void grantLeadership(UUID leaderSessionID);

  /**
   * Callback method which is called by the {@link LeaderElectionService} upon revoking the
   * leadership of a former leader. This might happen in case that multiple contenders have been
   * granted leadership.
   */
  void revokeLeadership();

  /**
   * Callback method which is called by {@link LeaderElectionService} in case of an error in the
   * service thread.
   *
   * @param exception Caught exception.
   */
  void handleError(Exception exception);

  /**
   * Returns the description of the {@link LeaderContender} for logging purpose.
   *
   * @return Description of this contender.
   */
  default String getDescription() {
    return "LeaderContender:" + getClass().getSimpleName();
  }
}

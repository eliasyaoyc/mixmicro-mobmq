package xyz.vopen.framework.neptune.core.leaderelection;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * {@link LeaderElectionService} Interface to a service which allows to elect a leader among a group
 * of contenders.
 *
 * <p>Prior to using this service, it has to be started calling the start method. The start method
 * takes the contender as parameter. If there are multiple contenders,then each contender has to
 * instantiate its own leader election service.
 *
 * <p>Once a contender has been granted leadership he has to confirm the received leader session ID
 * by calling the method {@link #confirmLeadership(UUID, String)}. This will notify the leader
 * election service, that the contender has accepted the leadership specified and that the leader
 * session id as well as the leader address can now be published for leader retrieval services.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public interface LeaderElectionService {

  /**
   * Starts the leader election service. This method can only be called once.
   *
   * @param contender LeaderContender which applies for the leadership.
   * @throws Exception
   */
  void start(LeaderContender contender) throws Exception;

  /**
   * Stops the leader election services.
   *
   * @throws Exception
   */
  void stop() throws Exception;

  /**
   * Confirms that the {@link LeaderContender} has accepted the leadership identified by the given
   * leader session id. It also publishes the leader address under which the leader is reachable.
   *
   * <p>The rational behind this method is to establish an order between setting the new leader
   * session ID in the {@link LeaderContender} and publishing the new leader session ID as well as
   * the leader address to the leader retrieval services.
   *
   * @param leaderSessionID The new leader session ID
   * @param leaderAddress The address of the new leader
   */
  void confirmLeadership(UUID leaderSessionID, String leaderAddress);

  /**
   * Returns true if the {@link LeaderContender} with which the services has been started owns
   * currently the leadership under the given leader session id.
   *
   * @param leaderSessionId identifying the current leader.
   * @return true if the associated {@link LeaderContender} is the leader, otherwise false.
   */
  boolean hasLeadership(@Nonnull UUID leaderSessionId);
}

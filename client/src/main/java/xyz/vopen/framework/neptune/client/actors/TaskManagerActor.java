package xyz.vopen.framework.neptune.client.actors;

import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.model.message.request.*;

/**
 * {@link TaskManagerActor} Used for receive request from JobManager and task.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class TaskManagerActor extends AbstractActor {
  private static final Logger logger = LoggerFactory.getLogger(TaskManagerActor.class);

  public Receive createReceive() {
    return receiveBuilder()
        .match(ReportTaskStatusRequest.class, this::reportTaskStatus)
        .match(DispatcherTaskRequest.class, this::dispatcherTask)
        .match(ReportHeartbeatRequest.class, this::reportHeartbeat)
        .match(StopTaskRequest.class, this::stopTask)
        .match(QueryTaskStatusRequest.class, this::queryTaskStatus)
        .build();
  }

  private void reportTaskStatus(ReportTaskStatusRequest taskStatusRequest) {}

  private void dispatcherTask(DispatcherTaskRequest dispatcherTaskRequest) {}

  private void reportHeartbeat(ReportHeartbeatRequest heartbeatRequest) {}

  private void stopTask(StopTaskRequest stopTaskRequest) {}

  private void queryTaskStatus(QueryTaskStatusRequest queryTaskStatusRequest) {}
}

package xyz.vopen.framework.neptune.client.actors;

import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.client.taskmaster.TaskManager;
import xyz.vopen.framework.neptune.common.model.message.request.TaskStartRequest;
import xyz.vopen.framework.neptune.common.model.message.request.TaskStopRequest;

/**
 * {@link SlotActor} Real Processor node that process requests from {@link TaskManager}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class SlotActor extends AbstractActor {
  private static final Logger logger = LoggerFactory.getLogger(SlotActor.class);

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(TaskStartRequest.class, this::receiveTaskStartRequest)
        .match(TaskStopRequest.class, this::receiveTaskStopRequest)
        .matchAny(request -> logger.warn("SlotActor receive unknown request :{}", request))
        .build();
  }

  private void receiveTaskStartRequest(TaskStartRequest taskStartRequest) {}

  private void receiveTaskStopRequest(TaskStopRequest taskStopRequest) {}
}

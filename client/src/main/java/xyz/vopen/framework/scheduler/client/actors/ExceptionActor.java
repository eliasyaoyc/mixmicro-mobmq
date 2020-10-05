package xyz.vopen.framework.scheduler.client.actors;

import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ExceptionActor} Processor system exception Actor.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
public class ExceptionActor extends AbstractActor {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionActor.class);

  public Receive createReceive() {
    return receiveBuilder().match(DeadLetter.class, this::processorDeadLetter).build();
  }

  private void processorDeadLetter(DeadLetter dl) {
    logger.warn("ExceptionActor receive DeadLetter : {}", dl);
  }
}

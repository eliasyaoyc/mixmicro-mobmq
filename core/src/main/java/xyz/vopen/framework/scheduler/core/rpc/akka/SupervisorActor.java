package xyz.vopen.framework.scheduler.core.rpc.akka;

import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SupervisorActor}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class SupervisorActor extends AbstractActor {
  private static final Logger logger = LoggerFactory.getLogger(SupervisorActor.class);

  @Override
  public Receive createReceive() {
    return null;
  }
}

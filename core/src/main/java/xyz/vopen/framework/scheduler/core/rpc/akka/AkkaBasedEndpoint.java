package xyz.vopen.framework.scheduler.core.rpc.akka;

import akka.actor.ActorRef;

/**
 * {@link AkkaBasedEndpoint} Interface for Akka based rpc gateways.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public interface AkkaBasedEndpoint {

  /**
   * Returns the {@link ActorRef} of the underlying RPC actor.
   *
   * @return the {@link ActorRef} of the underlying RPC actor.
   */
  ActorRef getActorRef();
}

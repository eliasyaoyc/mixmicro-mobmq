package xyz.vopen.framework.neptune.core.rpc.akka;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategyConfigurator;
import akka.japi.pf.PFBuilder;

/**
 * {@link EscalatingSupervisorStrategy} Escalating supervisor strategy.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class EscalatingSupervisorStrategy implements SupervisorStrategyConfigurator {

  @Override
  public SupervisorStrategy create() {
    return new OneForOneStrategy(
        false,
        new PFBuilder<Throwable, SupervisorStrategy.Directive>()
            .matchAny((ignored) -> (SupervisorStrategy.Directive) SupervisorStrategy.escalate())
            .build());
  }
}

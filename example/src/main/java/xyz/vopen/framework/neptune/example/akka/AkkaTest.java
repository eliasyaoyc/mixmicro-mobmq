package xyz.vopen.framework.neptune.example.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import xyz.vopen.framework.neptune.common.TestRequest;

/**
 * {@link AkkaTest} Test for akka
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/16
 */
public class AkkaTest {
  private static final String AKKA_PATH = "akka://%s@%s/user/rpc/%s";

  public static void main(String[] args) {

//    ActorSystem actorSystem = AkkaUtils.createDefaultActorSystem();


    Config akkaBasicConfig = ConfigFactory.load("test.akka.conf");

    ActorSystem actorSystem = ActorSystem.create("test",akkaBasicConfig);

    String akkaPath =
            String.format(AKKA_PATH, "neptune", "127.0.0.1:25520", "StandaloneDispatcher");
    ActorSelection actorSelection = actorSystem.actorSelection(akkaPath);
    actorSelection.tell(new TestRequest(), ActorRef.noSender());
  }
}

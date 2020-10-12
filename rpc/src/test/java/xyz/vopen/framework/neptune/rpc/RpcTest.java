package xyz.vopen.framework.neptune.rpc;

import akka.actor.ActorSystem;
import xyz.vopen.framework.neptune.common.time.Time;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcService;
import xyz.vopen.framework.neptune.rpc.akka.AkkaRpcServiceConfiguration;
import xyz.vopen.framework.neptune.rpc.akka.AkkaUtils;

/**
 * {@link RpcTest}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/9
 */
public class RpcTest {
  private static final Time timeout = Time.seconds(10L);
  private static ActorSystem actorSystem = null;
  private static RpcService rpcService = null;

  public interface HelloGateway extends RpcGateway {
    String hello();
  }

  public interface HiGateway extends RpcGateway {
    String hi();
  }

  public static class HelloEndpoint extends RpcEndpoint implements HelloGateway {

    protected HelloEndpoint(RpcService rpcService) {
      super(rpcService);
    }

    @Override
    public String hello() {
      return "hello";
    }
  }

  public static class HiRpcEndpoint extends RpcEndpoint implements HiGateway{

    protected HiRpcEndpoint(RpcService rpcService){
      super(rpcService);
    }

    @Override
    public String hi() {
      return "hi";
    }
  }

  public static void main(String[] args) throws Exception{
    actorSystem = AkkaUtils.createDefaultActorSystem();
    rpcService = new AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.defaultConfiguration());

    HelloEndpoint helloEndpoint = new HelloEndpoint(rpcService);
    helloEndpoint.start();
    HelloGateway selfGateway = helloEndpoint.getSelfGateway(HelloGateway.class);
    String hello = selfGateway.hello();
    System.out.println(hello);

//    HiRpcEndpoint hiRpcEndpoint = new HiRpcEndpoint(rpcService);
//    hiRpcEndpoint.start();
//    HiGateway hiGateway = rpcService.connect(hiRpcEndpoint.getAddress(), HiGateway.class).get();
//    String hi = hiGateway.hi();
//    System.out.println(hi);
  }
}

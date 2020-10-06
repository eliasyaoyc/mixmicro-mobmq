package xyz.vopen.framework.scheduler.core.rpc.akka;

import xyz.vopen.framework.scheduler.core.configuration.Configuration;

/**
 * {@link AkkaRpcServiceUtils}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaRpcServiceUtils {

  private static final String SIMPLE_AKKA_CONFIG_TEMPLATE =
      "akka {remote {netty.tcp {maximum-frame-size = %s}}}";

  private static final String MAXIMUM_FRAME_SIZE_PATH = "akka.remote.netty.tcp.maximum-frame-size";

  public static long extractMaximumFrameSize(Configuration configuration) {
    //    String maxFrameSizeStr = configuration.getString(AkkaOptions.FRAMESIZE);
    //    String akkaConfigStr = String.format(SIMPLE_AKKA_CONFIG_TEMPLATE, maxFrameSizeStr);
    //    Config akkaConfig = ConfigFactory.parseString(akkaConfigStr);
    //    return akkaConfig.getBytes(MAXIMUM_FRAME_SIZE_PATH);
    return 0;
  }
}

package xyz.vopen.framework.neptune.core.rpc.akka;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import xyz.vopen.framework.neptune.core.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.core.configuration.Configuration;

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
    String maxFrameSizeStr = configuration.getString(AkkaOptions.FRAMESIZE);
    String akkaConfigStr = String.format(SIMPLE_AKKA_CONFIG_TEMPLATE, maxFrameSizeStr);
    Config akkaConfig = ConfigFactory.parseString(akkaConfigStr);
    return akkaConfig.getBytes(MAXIMUM_FRAME_SIZE_PATH);
  }
}

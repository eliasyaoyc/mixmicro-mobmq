package xyz.vopen.framework.neptune.rpc.akka;

import xyz.vopen.framework.neptune.common.configuration.AkkaOptions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.utils.time.Time;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link AkkaRpcServiceConfiguration} Configuration for the {@link AkkaRpcService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaRpcServiceConfiguration {

  private final @NotNull Time timeout;

  private final long maximumFrameSize;

  private final boolean captureAskCallStack;

  public AkkaRpcServiceConfiguration(
      @NotNull Time timeout, long maximumFrameSize, boolean captureAskCallStack) {

    checkArgument(maximumFrameSize > 0L, "Maximum frameSize must be positive.");
    this.timeout = timeout;
    this.maximumFrameSize = maximumFrameSize;
    this.captureAskCallStack = captureAskCallStack;
  }

  public @NotNull Time getTimeout() {
    return this.timeout;
  }

  public long getMaximumFrameSize() {
    return this.maximumFrameSize;
  }

  public boolean captureAskCallStack() {
    return this.captureAskCallStack;
  }

  public static AkkaRpcServiceConfiguration fromConfiguration(Configuration configuration) {
    final Time timeout = AkkaUtils.getTimeoutAsTime(configuration);

    final long maximumFrameSize = AkkaRpcServiceUtils.extractMaximumFrameSize(configuration);

    final boolean captureAskCallStacks =
        (boolean) configuration.get(AkkaOptions.CAPTURE_ASK_CALLSTACK);

    return new AkkaRpcServiceConfiguration(timeout, maximumFrameSize, captureAskCallStacks);
  }

  public static AkkaRpcServiceConfiguration defaultConfiguration() {
    return fromConfiguration(new Configuration());
  }
}

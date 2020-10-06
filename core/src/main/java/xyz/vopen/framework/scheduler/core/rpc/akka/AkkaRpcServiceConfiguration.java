package xyz.vopen.framework.scheduler.core.rpc.akka;

import xyz.vopen.framework.scheduler.common.time.Time;
import xyz.vopen.framework.scheduler.core.configuration.Configuration;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link AkkaRpcServiceConfiguration} Configuration for the {@link AkkaRpcService}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class AkkaRpcServiceConfiguration {

  private final @NotNull Configuration configuration;

  private final @NotNull Time timeout;

  private final long maximumFrameSize;

  private final boolean captureAskCallStack;

  public AkkaRpcServiceConfiguration(
      @NotNull Configuration configuration,
      @NotNull Time timeout,
      long maximumFrameSize,
      boolean captureAskCallStack) {

    checkArgument(maximumFrameSize > 0L, "Maximum frameSize must be positive.");
    this.configuration = configuration;
    this.timeout = timeout;
    this.maximumFrameSize = maximumFrameSize;
    this.captureAskCallStack = captureAskCallStack;
  }

  public @NotNull Configuration getConfiguration() {
    return this.configuration;
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
    return null;
  }

  public static AkkaRpcServiceConfiguration defaultConfiguration() {
    return fromConfiguration(new Configuration());
  }
}

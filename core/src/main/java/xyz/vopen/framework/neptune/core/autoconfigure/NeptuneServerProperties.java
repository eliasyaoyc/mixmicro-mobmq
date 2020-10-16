package xyz.vopen.framework.neptune.core.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static xyz.vopen.framework.neptune.core.autoconfigure.NeptuneServerProperties.MIXMICRO_NEPTUNE_SERVER_PROPERTIES_PREFIX;

/**
 * {@link NeptuneServerProperties}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@ConfigurationProperties(prefix = MIXMICRO_NEPTUNE_SERVER_PROPERTIES_PREFIX)
public class NeptuneServerProperties {
  static final String MIXMICRO_NEPTUNE_SERVER_PROPERTIES_PREFIX = "mixmicro.neptune.server";

  private String jobManagerRpcAddress;
  private String jobManagerRpcPort;
  private String jobManagerBindHost;
  private String jobManagerRpcBindPort;

  public String getJobManagerRpcAddress() {
    return jobManagerRpcAddress;
  }

  public void setJobManagerRpcAddress(String jobManagerRpcAddress) {
    this.jobManagerRpcAddress = jobManagerRpcAddress;
  }

  public String getJobManagerRpcPort() {
    return jobManagerRpcPort;
  }

  public void setJobManagerRpcPort(String jobManagerRpcPort) {
    this.jobManagerRpcPort = jobManagerRpcPort;
  }

  public String getJobManagerBindHost() {
    return jobManagerBindHost;
  }

  public void setJobManagerBindHost(String jobManagerBindHost) {
    this.jobManagerBindHost = jobManagerBindHost;
  }

  public String getJobManagerRpcBindPort() {
    return jobManagerRpcBindPort;
  }

  public void setJobManagerRpcBindPort(String jobManagerRpcBindPort) {
    this.jobManagerRpcBindPort = jobManagerRpcBindPort;
  }
}

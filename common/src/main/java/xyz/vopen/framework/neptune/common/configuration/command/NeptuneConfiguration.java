package xyz.vopen.framework.neptune.common.configuration.command;

import javax.annotation.Nonnull;

/**
 * {@link NeptuneConfiguration} Configuration for the Neptune.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class NeptuneConfiguration {
  private @Nonnull String jobManagerRpcAddress;
  private String jobManagerRpcPort;
  private String jobManagerBindHost;
  private String jobManagerRpcBindPort;

  public NeptuneConfiguration(
      @Nonnull String jobManagerRpcAddress,
      String jobManagerRpcPort,
      String jobManagerBindHost,
      String jobManagerRpcBindPort) {
    this.jobManagerRpcAddress = jobManagerRpcAddress;
    this.jobManagerRpcPort = jobManagerRpcPort;
    this.jobManagerBindHost = jobManagerBindHost;
    this.jobManagerRpcBindPort = jobManagerRpcBindPort;
  }

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

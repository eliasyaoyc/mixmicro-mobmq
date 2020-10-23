package xyz.vopen.framework.neptune.common.event;

import xyz.vopen.framework.neptune.common.enums.InstanceStatus;

/**
 * {@link JobStatusChangeEvent}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/23
 */
public class JobStatusChangeEvent extends Event {
  private long instanceId;
  private long jobId;
  private InstanceStatus status;
  private String cause;

  private JobStatusChangeEvent(long instanceId, long jobId, InstanceStatus status, String cause) {
    this.instanceId = instanceId;
    this.jobId = jobId;
    this.status = status;
    this.cause = cause;
  }

  public long getInstanceId() {
    return instanceId;
  }

  public long getJobId() {
    return jobId;
  }

  public InstanceStatus getStatus() {
    return status;
  }

  public String getCause() {
    return cause;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private long instanceId;
    private long jobId;
    private InstanceStatus status;
    private String cause;

    public Builder withInstanceId(long instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    public Builder withJobId(long jobId) {
      this.jobId = jobId;
      return this;
    }

    public Builder withStatus(InstanceStatus status) {
      this.status = status;
      return this;
    }

    public Builder withCause(String cause) {
      this.cause = cause;
      return this;
    }

    public JobStatusChangeEvent build() {
      return new JobStatusChangeEvent(instanceId, jobId, status, cause);
    }
  }
}

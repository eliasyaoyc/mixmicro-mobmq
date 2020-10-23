package xyz.vopen.framework.neptune.common.event;

import xyz.vopen.framework.neptune.common.model.JobInfo;

/**
 * {@link DispatchJobEvent}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/23
 */
public class DispatchJobEvent extends Event {
  private JobInfo jobInfo;
  private long instanceId;
  private long runningTimes;
  private String instanceParams;
  private long workFlowId;

  private DispatchJobEvent(
      JobInfo jobInfo, long instanceId, long runningTimes, String instanceParams, long workFlowId) {
    this.jobInfo = jobInfo;
    this.instanceId = instanceId;
    this.runningTimes = runningTimes;
    this.instanceParams = instanceParams;
    this.workFlowId = workFlowId;
  }

  public JobInfo getJobInfo() {
    return jobInfo;
  }

  public long getInstanceId() {
    return instanceId;
  }

  public long getRunningTimes() {
    return runningTimes;
  }

  public String getInstanceParams() {
    return instanceParams;
  }

  public long getWorkFlowId() {
    return workFlowId;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private JobInfo jobInfo;
    private long instanceId;
    private long runningTimes;
    private String instanceParams;
    private long workFlowId;

    public Builder withJobInfo(JobInfo jobInfo) {
      this.jobInfo = jobInfo;
      return this;
    }

    public Builder withInstanceId(long instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    public Builder withRunningTimes(long runningTimes) {
      this.runningTimes = runningTimes;
      return this;
    }

    public Builder withInstanceParams(String instanceParams) {
      this.instanceParams = instanceParams;
      return this;
    }

    public Builder withWorkFlowId(long workFlowId) {
      this.workFlowId = workFlowId;
      return this;
    }

    public DispatchJobEvent build() {
      return new DispatchJobEvent(jobInfo, instanceId, runningTimes, instanceParams, workFlowId);
    }
  }
}

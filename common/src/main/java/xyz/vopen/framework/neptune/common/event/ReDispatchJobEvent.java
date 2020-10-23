package xyz.vopen.framework.neptune.common.event;

import xyz.vopen.framework.neptune.common.model.JobInfo;

/**
 * {@link ReDispatchJobEvent}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/23
 */
public class ReDispatchJobEvent extends Event {
  private JobInfo jobInfo;
  private long instanceId;
  private long runningTimes;

  private ReDispatchJobEvent(JobInfo jobInfo, long instanceId, long runningTimes) {
    this.jobInfo = jobInfo;
    this.instanceId = instanceId;
    this.runningTimes = runningTimes;
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private JobInfo jobInfo;
    private long instanceId;
    private long runningTimes;

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

    public ReDispatchJobEvent build() {
      return new ReDispatchJobEvent(jobInfo, instanceId, runningTimes);
    }
  }
}

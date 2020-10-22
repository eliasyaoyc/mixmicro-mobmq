package xyz.vopen.framework.neptune.common.model;

import java.util.Date;

/**
 * {@link InstanceInfo} Represent job execute record.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
public class InstanceInfo {
  private Long id;
  /** The app id to which the task belongs. */
  private Long appId;
  /** The app id to which the task belongs. */
  private Long jobId;
  /** The params of job. */
  private String jobParams;

  /** The type of the task instance, normal/workflow */
  private Integer type;
  /** The workflow ID to which the task instance belongs, only the workflow task exists */
  private Long workFlowId;

  /**
   * 1-waiting dispatch
   *
   * <p>2-waiting worker receive
   *
   * <p>3-running
   *
   * <p>4-failed
   *
   * <p>5-success
   *
   * <p>9-cancel
   *
   * <p>10-manual stop.
   */
  private Integer status;
  /** Execution results (allowing to store larger results) */
  private String result;
  /** Trigger time */
  private Date TriggerTime;

  private Date completedTime;

  private Date lastReportTime;

  private Date executeTime;

  private Integer retryTimes;

  private String taskAddress;

  private Date gmtCreate;
  private Date gmtModified;

  public InstanceInfo(
      Long id,
      Long appId,
      Long jobId,
      String jobParams,
      Integer type,
      Long workFlowId,
      Integer status,
      String result,
      Date triggerTime,
      Date completedTime,
      Date lastReportTime,
      Date executeTime,
      Integer retryTimes,
      String taskAddress,
      Date gmtCreate,
      Date gmtModified) {
    this.id = id;
    this.appId = appId;
    this.jobId = jobId;
    this.jobParams = jobParams;
    this.type = type;
    this.workFlowId = workFlowId;
    this.status = status;
    this.result = result;
    TriggerTime = triggerTime;
    this.completedTime = completedTime;
    this.lastReportTime = lastReportTime;
    this.executeTime = executeTime;
    this.retryTimes = retryTimes;
    this.taskAddress = taskAddress;
    this.gmtCreate = gmtCreate;
    this.gmtModified = gmtModified;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAppId() {
    return appId;
  }

  public void setAppId(Long appId) {
    this.appId = appId;
  }

  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public String getJobParams() {
    return jobParams;
  }

  public void setJobParams(String jobParams) {
    this.jobParams = jobParams;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getWorkFlowId() {
    return workFlowId;
  }

  public void setWorkFlowId(Long workFlowId) {
    this.workFlowId = workFlowId;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Date getTriggerTime() {
    return TriggerTime;
  }

  public void setTriggerTime(Date triggerTime) {
    TriggerTime = triggerTime;
  }

  public Date getCompletedTime() {
    return completedTime;
  }

  public void setCompletedTime(Date completedTime) {
    this.completedTime = completedTime;
  }

  public Date getLastReportTime() {
    return lastReportTime;
  }

  public void setLastReportTime(Date lastReportTime) {
    this.lastReportTime = lastReportTime;
  }

  public Date getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Date executeTime) {
    this.executeTime = executeTime;
  }

  public Integer getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(Integer retryTimes) {
    this.retryTimes = retryTimes;
  }

  public String getTaskAddress() {
    return taskAddress;
  }

  public void setTaskAddress(String taskAddress) {
    this.taskAddress = taskAddress;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }
}

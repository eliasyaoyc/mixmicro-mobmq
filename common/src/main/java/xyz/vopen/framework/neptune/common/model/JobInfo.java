package xyz.vopen.framework.neptune.common.model;

import xyz.vopen.framework.neptune.common.utils.IdGenerateUtil;

import java.util.Date;

/**
 * {@link JobInfo} Represent a scheduler job that wait to execute.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class JobInfo {
  /** Unique id through {@link IdGenerateUtil#generate()} generate. */
  private Long id;
  /** The name of job. */
  private String jobName;
  /** The description of job. */
  private String jobDescription;
  /** The app id to which the task belongs. */
  private Long appId;
  /** The params of job. */
  private String jobParams;
  /** The type of time expression (CRON/API/FIX_RATE/FIX_DELAY) */
  private Integer timeExpressionType;
  /** Time expression, CRON/NULL/LONG/LONG */
  private String timeExpression;
  /** The type of execution, stand-alone/broadcast/mapreduce. */
  private Integer executeType;
  /** The type of processor，Java/Shell */
  private Integer processorType;
  /** The executor information,which may require the entire script file to be stored. */
  private String processorInfo;
  /** Maximum number of simultaneously running tasks, default 1. */
  private Integer maxInstanceNum;
  /** The maximum number of threads that perform a task at the same time. */
  private Integer concurrency;
  /** The overall timeout of the task. */
  private Long instanceTimeLimit;

  private Integer instanceRetryNum;
  private Integer taskRetryNum;

  /** 1 normal running, 2 stop */
  private Integer status;
  /** Next trigger time. */
  private Long nextTriggerTime;

  /** Minimum CPU core number, 0 for unlimited. */
  private double minCpuCores;
  // 最低内存空间，单位 GB，0代表不限
  /** Minimum memory space, GB per unit, 0 represents an unlimited. */
  private double minMemorySpace;
  /** Minimum disk space, GB per unit, 0 represents an unlimited. */
  private double minDiskSpace;

  /**
   * Specifies that the machine is running, that there is no limit to the number of empty delegates,
   * and that the non-empty will only use the machine in it to run (multi-value comma segmentation)
   */
  private String designatedWorkers;
  /** The maximum number of machines */
  private Integer maxWorkerCount;

  /** Alarm user ID list, multi-value comma. */
  private String notifyUserIds;

  private Date gmtCreate;
  private Date gmtUpdate;

  public JobInfo(
      Long id,
      String jobName,
      String jobDescription,
      Long appId,
      String jobParams,
      Integer timeExpressionType,
      String timeExpression,
      Integer executeType,
      Integer processorType,
      String processorInfo,
      Integer maxInstanceNum,
      Integer concurrency,
      Long instanceTimeLimit,
      Integer instanceRetryNum,
      Integer taskRetryNum,
      Integer status,
      Long nextTriggerTime,
      double minCpuCores,
      double minMemorySpace,
      double minDiskSpace,
      String designatedWorkers,
      Integer maxWorkerCount,
      String notifyUserIds,
      Date gmtCreate,
      Date gmtUpdate) {
    this.id = id;
    this.jobName = jobName;
    this.jobDescription = jobDescription;
    this.appId = appId;
    this.jobParams = jobParams;
    this.timeExpressionType = timeExpressionType;
    this.timeExpression = timeExpression;
    this.executeType = executeType;
    this.processorType = processorType;
    this.processorInfo = processorInfo;
    this.maxInstanceNum = maxInstanceNum;
    this.concurrency = concurrency;
    this.instanceTimeLimit = instanceTimeLimit;
    this.instanceRetryNum = instanceRetryNum;
    this.taskRetryNum = taskRetryNum;
    this.status = status;
    this.nextTriggerTime = nextTriggerTime;
    this.minCpuCores = minCpuCores;
    this.minMemorySpace = minMemorySpace;
    this.minDiskSpace = minDiskSpace;
    this.designatedWorkers = designatedWorkers;
    this.maxWorkerCount = maxWorkerCount;
    this.notifyUserIds = notifyUserIds;
    this.gmtCreate = gmtCreate;
    this.gmtUpdate = gmtUpdate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getJobDescription() {
    return jobDescription;
  }

  public void setJobDescription(String jobDescription) {
    this.jobDescription = jobDescription;
  }

  public Long getAppId() {
    return appId;
  }

  public void setAppId(Long appId) {
    this.appId = appId;
  }

  public String getJobParams() {
    return jobParams;
  }

  public void setJobParams(String jobParams) {
    this.jobParams = jobParams;
  }

  public Integer getTimeExpressionType() {
    return timeExpressionType;
  }

  public void setTimeExpressionType(Integer timeExpressionType) {
    this.timeExpressionType = timeExpressionType;
  }

  public String getTimeExpression() {
    return timeExpression;
  }

  public void setTimeExpression(String timeExpression) {
    this.timeExpression = timeExpression;
  }

  public Integer getExecuteType() {
    return executeType;
  }

  public void setExecuteType(Integer executeType) {
    this.executeType = executeType;
  }

  public Integer getProcessorType() {
    return processorType;
  }

  public void setProcessorType(Integer processorType) {
    this.processorType = processorType;
  }

  public String getProcessorInfo() {
    return processorInfo;
  }

  public void setProcessorInfo(String processorInfo) {
    this.processorInfo = processorInfo;
  }

  public Integer getMaxInstanceNum() {
    return maxInstanceNum;
  }

  public void setMaxInstanceNum(Integer maxInstanceNum) {
    this.maxInstanceNum = maxInstanceNum;
  }

  public Integer getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(Integer concurrency) {
    this.concurrency = concurrency;
  }

  public Long getInstanceTimeLimit() {
    return instanceTimeLimit;
  }

  public void setInstanceTimeLimit(Long instanceTimeLimit) {
    this.instanceTimeLimit = instanceTimeLimit;
  }

  public Integer getInstanceRetryNum() {
    return instanceRetryNum;
  }

  public void setInstanceRetryNum(Integer instanceRetryNum) {
    this.instanceRetryNum = instanceRetryNum;
  }

  public Integer getTaskRetryNum() {
    return taskRetryNum;
  }

  public void setTaskRetryNum(Integer taskRetryNum) {
    this.taskRetryNum = taskRetryNum;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Long getNextTriggerTime() {
    return nextTriggerTime;
  }

  public void setNextTriggerTime(Long nextTriggerTime) {
    this.nextTriggerTime = nextTriggerTime;
  }

  public double getMinCpuCores() {
    return minCpuCores;
  }

  public void setMinCpuCores(double minCpuCores) {
    this.minCpuCores = minCpuCores;
  }

  public double getMinMemorySpace() {
    return minMemorySpace;
  }

  public void setMinMemorySpace(double minMemorySpace) {
    this.minMemorySpace = minMemorySpace;
  }

  public double getMinDiskSpace() {
    return minDiskSpace;
  }

  public void setMinDiskSpace(double minDiskSpace) {
    this.minDiskSpace = minDiskSpace;
  }

  public String getDesignatedWorkers() {
    return designatedWorkers;
  }

  public void setDesignatedWorkers(String designatedWorkers) {
    this.designatedWorkers = designatedWorkers;
  }

  public Integer getMaxWorkerCount() {
    return maxWorkerCount;
  }

  public void setMaxWorkerCount(Integer maxWorkerCount) {
    this.maxWorkerCount = maxWorkerCount;
  }

  public String getNotifyUserIds() {
    return notifyUserIds;
  }

  public void setNotifyUserIds(String notifyUserIds) {
    this.notifyUserIds = notifyUserIds;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtUpdate() {
    return gmtUpdate;
  }

  public void setGmtUpdate(Date gmtUpdate) {
    this.gmtUpdate = gmtUpdate;
  }
}

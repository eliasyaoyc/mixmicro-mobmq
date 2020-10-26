package xyz.vopen.framework.neptune.core.schedule;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.constants.InstanceResult;
import xyz.vopen.framework.neptune.common.enums.ExpressionType;
import xyz.vopen.framework.neptune.common.enums.JobStatus;
import xyz.vopen.framework.neptune.common.model.InstanceInfo;
import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.common.model.event.DispatchJobEvent;
import xyz.vopen.framework.neptune.common.model.event.JobStatusChangeEvent;
import xyz.vopen.framework.neptune.common.model.event.ReDispatchJobEvent;
import xyz.vopen.framework.neptune.common.utils.*;
import xyz.vopen.framework.neptune.common.utils.time.timewheel.HashedWheelTimer;
import xyz.vopen.framework.neptune.core.persistence.Persistence;
import xyz.vopen.framework.neptune.core.persistence.adapter.PersistenceAdapter;
import xyz.vopen.framework.neptune.rpc.RpcService;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static xyz.vopen.framework.neptune.common.enums.InstanceStatus.*;

/**
 * {@link SchedulerService} Unified time task service that used for check the job status, clean the
 * expire log etc.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
public class SchedulerService {
  private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
  private static final long SERVER_STATUS_CHECKER_INITIAL_DELAY = 5000;
  private static final long SERVER_STATUS_CHECKER_DELAY = 10000;
  private static final long TASK_ACQUIRE_INITIAL_DELAY = 2000;
  private static final long TASK_ACQUIRE_DELAY = 5000;
  private static final String SCHEDULER_THREAD_POOL_NAME = "scheduler";
  private static final int MAX_BATCH = 10;
  private static final long DISPATCH_TIMEOUT_MS = 30000;

  private final @Nonnull Configuration configuration;
  private final @Nonnull RpcService rpcService;
  private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
  final PersistenceAdapter persistenceAdapter;
  private HashedWheelTimer hashedWheelTimer;
  private final EventBus eventBus;

  public SchedulerService(
      final @Nonnull Configuration configuration,
      final @Nonnull RpcService rpcService,
      final @Nonnull Persistence persistence,
      final EventBus eventBus) {
    this.configuration = configuration;
    this.rpcService = rpcService;
    this.persistenceAdapter = persistence.getPersistenceAdapter();
    this.eventBus = eventBus;
    this.hashedWheelTimer = HashedWheelTimer.defaultWheelTimer();
    this.scheduledThreadPoolExecutor =
        new ScheduledThreadPoolExecutor(3, new ExecutorThreadFactory(SCHEDULER_THREAD_POOL_NAME));
  }

  public void start() {
    scheduledThreadPoolExecutor.scheduleWithFixedDelay(
        new TasksStatusChecker(),
        SERVER_STATUS_CHECKER_INITIAL_DELAY,
        SERVER_STATUS_CHECKER_DELAY,
        TimeUnit.MILLISECONDS);
    scheduledThreadPoolExecutor.scheduleWithFixedDelay(
        new TaskAcquirer(), TASK_ACQUIRE_INITIAL_DELAY, TASK_ACQUIRE_DELAY, TimeUnit.MILLISECONDS);
    scheduledThreadPoolExecutor.schedule(new LogCleaner(), 7, TimeUnit.DAYS);
  }

  public void stop() {
    if (scheduledThreadPoolExecutor != null) {
      ExecutorStUtil.gracefulShutdown(5000, TimeUnit.MILLISECONDS, scheduledThreadPoolExecutor);
    }
  }

  /**
   * Add the job that needed to allocated to purgatory. Purgatory that time wheel stores time task.
   * the first job is the fastest execution. The Job is distributed the specified worker when the
   * trigger time is reached.
   *
   * @param jobInfos time task collection.
   */
  public void addJobToPurgatory(@Nonnull List<JobInfo> jobInfos) {
    jobInfos.parallelStream().forEach(this::addJobToPurgatory);
  }

  public void addJobToPurgatory(@Nonnull JobInfo job) {}

  /**
   * Check the Job status, excluded exception job。
   *
   * @param jobInfos The {@link JobInfo} collection.
   */
  private void checkStatus(@Nonnull List<JobInfo> jobInfos) {
    Lists.partition(jobInfos, MAX_BATCH)
        .forEach(
            partJobInfos -> {
              Optional<List<InstanceInfo>> results =
                  persistenceAdapter.findInstancesByAppId(
                      Integer.parseInt(NetUtils.getLocalAddress().toString()));
              if (!results.isPresent()) {
                return;
              }
              List<InstanceInfo> instanceInfos = results.get();
              for (InstanceInfo instanceInfo : instanceInfos) {
                // 1. check the job which the status is WAITING_DISPATCH.
                if (instanceInfo.getStatus() == WAITING_DISPATCH.getStatus()) {
                  long time = Instant.now().toEpochMilli() - instanceInfo.getGmtUpdate().getTime();
                  // case1 dispatch failure. skip it.
                  if (time < DISPATCH_TIMEOUT_MS) {
                    return;
                  }

                  Optional<JobInfo> jobInfo =
                      persistenceAdapter.findJobById(instanceInfo.getJobId());
                  if (jobInfo.isPresent()) {
                    // dispatch job.
                    eventBus.post(DispatchJobEvent.builder().build());
                  }
                  // 2. check
                } else if (instanceInfo.getStatus() == WAITING_WORKER_RECEIVE.getStatus()) {
                  LOG.warn(
                      "[checkStatus] instance {} didn't receive any reply from worker",
                      instanceInfo.getId());
                  Optional<JobInfo> job = persistenceAdapter.findJobById(instanceInfo.getJobId());
                  if (job.isPresent()) {
                    // redispatch job.
                    eventBus.post(ReDispatchJobEvent.builder().build());
                  }

                } else if (instanceInfo.getStatus() == RUNNING.getStatus()) {
                  // 检查 RUNNING 状态的任务（一定时间没收到 worker 的状态报告，视为失败）
                  if (instanceInfo.getGmtUpdate().getTime()
                      < Instant.now().toEpochMilli() - DISPATCH_TIMEOUT_MS) {
                    JobInfo jobInfo =
                        persistenceAdapter
                            .findJobById(instanceInfo.getJobId())
                            .orElseGet(JobInfo::new);

                    if (jobInfo.getStatus() != 1
                        || (jobInfo.getTimeExpressionType() == 4
                            || jobInfo.getTimeExpressionType() == 5)) {
                      updateFailedInstance(instanceInfo);
                      return;
                    }

                    if (instanceInfo.getRetryTimes() < jobInfo.getInstanceRetryNum()) {
                      // redispatch
                      eventBus.post(ReDispatchJobEvent.builder().build());
                    } else {
                      updateFailedInstance(instanceInfo);
                    }
                  }
                }
              }
            });
  }

  /**
   * Process failed instance that report timeout.
   *
   * @param instanceInfo
   */
  private void updateFailedInstance(InstanceInfo instanceInfo) {
    LOG.warn(
        "[checkStatus] detected instance(id={},jobId={}) report timeout,this instance is considered a failure.",
        instanceInfo.getId(),
        instanceInfo.getJobId());

    instanceInfo.setStatus(FAILED.getStatus());
    instanceInfo.setCompletedTime(new Date());
    instanceInfo.setResult(InstanceResult.WORKER_REPORT_TIMEOUT);

    persistenceAdapter.updateInstanceInfo(instanceInfo);

    // publish job failure event.
    eventBus.post(
        JobStatusChangeEvent.builder()
            .withInstanceId(instanceInfo.getId())
            .withJobId(instanceInfo.getJobId())
            .withStatus(FAILED)
            .withCause(InstanceResult.WORKER_REPORT_TIMEOUT)
            .build());
  }

  /**
   * Schedule job that the expression type is CRON.
   *
   * @param jobInfo {@link JobInfo} instance.
   */
  private void scheduleCronJob(JobInfo jobInfo) {
    InstanceInfo instanceInfo = generateInstanceRecord(jobInfo);
    persistenceAdapter.saveInstanceInfo(instanceInfo);
    LOG.info("[scheduleCronJob] The cron job will be scheduled： {}.", jobInfo);

    long nextTriggerTime = jobInfo.getNextTriggerTime();
    long delay = 0;
    long now = Instant.now().toEpochMilli();
    if (nextTriggerTime < now) {
      LOG.warn(
          "[Job-{}] schedule delay, expect: {}, current: {}",
          jobInfo.getId(),
          nextTriggerTime,
          now);
    } else {
      delay = nextTriggerTime - now;
    }
    hashedWheelTimer.schedule(
        () -> {
          eventBus.post(DispatchJobEvent.builder().build());
        },
        delay,
        TimeUnit.MILLISECONDS);

    refreshJob(jobInfo);
  }

  /**
   * Schedule job that the type is workflow.
   *
   * @param jobInfo {@link JobInfo} instance.
   */
  private void scheduleWorkflow(JobInfo jobInfo) {}

  /**
   * Schedule job that the frequent execute.
   *
   * @param jobInfo {@link JobInfo} instance.
   */
  private void scheduleFrequentJob(JobInfo jobInfo) {}

  /**
   * Calculate the next scheduling time (ignoring repeated executions within 5S, that is, the
   * smallest continuous execution interval in CRON mode is SCHEDULE_RATE ms)
   *
   * @param jobInfo {@link JobInfo} instance.
   */
  private void refreshJob(JobInfo jobInfo) {
    Date nextTriggerTime =
        calculateNextTriggerTime(
            jobInfo.getNextTriggerTime(),
            jobInfo.getTimeExpressionType(),
            jobInfo.getTimeExpression());

    if (nextTriggerTime == null) {
      LOG.warn(
          "[Job-{}] this job won't be scheduled anymore, system will set the status to DISABLE!",
          jobInfo.getId());
      jobInfo.setStatus(JobStatus.STOP.getStatus());
    } else {
      jobInfo.setNextTriggerTime(nextTriggerTime.getTime());
    }
    persistenceAdapter.saveJobInfo(jobInfo);
  }

  private InstanceInfo generateInstanceRecord(JobInfo jobInfo) {
    return InstanceInfo.builder()
        .id(IdGenerateUtil.generate())
        .appId(jobInfo.getAppId())
        .jobId(jobInfo.getId())
        .jobParams(jobInfo.getJobParams())
        .triggerTime(
            calculateNextTriggerTime(
                Instant.now().toEpochMilli(),
                jobInfo.getTimeExpressionType(),
                jobInfo.getTimeExpression()))
        .status(WAITING_DISPATCH.getStatus())
        .type(jobInfo.getStatus())
        .retryTimes(0)
        .build();
  }

  /**
   * Returns the time of the next trigger that to be calculated by the time expression and the
   * specific delay time.
   *
   * @param preTriggerTime last trigger time.
   * @param timeExpressionType The time expression, CRON/API/FIX_RATE/FIX_DELAY
   * @param timeExpression Concrete time delay, CRON/NULL/LONG/LONG
   * @return The next trigger time.
   */
  public Date calculateNextTriggerTime(
      @Nonnull Long preTriggerTime,
      @Nonnull Integer timeExpressionType,
      @Nonnull String timeExpression) {
    switch (timeExpressionType) {
      case ExpressionType.CRON:
        try {
          CronExpression ce = new CronExpression(timeExpression);
          // NOTE: Take the maximum value to prevent continuous scheduling of unscheduled tasks for
          // a long time (the original DISABLE task is suddenly opened, and if the maximum value is
          // not taken, all past scheduling will be supplemented)
          long benchmarkTime = Math.max(System.currentTimeMillis(), preTriggerTime);
          return ce.getNextValidTimeAfter(new Date(benchmarkTime));
        } catch (ParseException e) {
          e.printStackTrace();
        }
      case ExpressionType.API:
      case ExpressionType.FIX_RATE:
      case ExpressionType.FIX_DELAY:
      default:
        throw new IllegalArgumentException();
    }
  }
  // ===================== SCHEDULERS =====================

  /** Used to check the status of tasks that under the current server. */
  class TasksStatusChecker implements Runnable {
    @Override
    public void run() {
      Stopwatch stopwatch = Stopwatch.createStarted();
      Optional<List<JobInfo>> jobInfos =
          persistenceAdapter.findJobByAppId(
              Integer.parseInt(NetUtils.getLocalAddress().toString()));

      if (!jobInfos.isPresent()) {
        LOG.info("[TaskAcquirer] current server has no job to check");
        return;
      }

      try {
        checkStatus(jobInfos.get());

      } catch (Exception e) {
        LOG.info(
            "[TaskAcquirer] job status check failed, cause: {}",
            ExceptionUtil.stringifyException(e));
      }
      LOG.info("[TaskAcquirer] job check used {}", stopwatch.stop());
    }
  }

  /** Used to obtain the task that belongs to current server. */
  class TaskAcquirer implements Runnable {
    @Override
    public void run() {
      Optional<List<JobInfo>> jobs =
          persistenceAdapter.findJobByAppIdAndStatus(
              Integer.parseInt(NetUtils.getLocalAddress().toString()), JobStatus.NEW.getStatus());
      if (!jobs.isPresent()) {
        return;
      }

      for (JobInfo jobInfo : jobs.get()) {
        jobInfo.setStatus(JobStatus.RUNNING.getStatus());
        try {
          persistenceAdapter.updateJobInfo(jobInfo);
        } catch (Exception e) {
          LOG.error(
              "[updateInstanceInfo] occur error, err: {}", ExceptionUtil.stringifyException(e));
        }

        try {
          if (jobInfo.getTimeExpressionType() == ExpressionType.CRON) {
            scheduleCronJob(jobInfo);
          }
        } catch (Exception e) {
          LOG.error("[scheduleCronJob] schedule cron job failed.", e);
        }
      }
    }
  }

  /** Used to clean expire log. */
  class LogCleaner implements Runnable {
    @Override
    public void run() {}
  }
}

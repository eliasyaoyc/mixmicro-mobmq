package xyz.vopen.framework.neptune.core.schedule;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.common.model.ServerInfo;
import xyz.vopen.framework.neptune.common.time.timewheel.HashedWheelTimer;
import xyz.vopen.framework.neptune.common.utils.ExceptionUtil;
import xyz.vopen.framework.neptune.common.utils.ExecutorStUtil;
import xyz.vopen.framework.neptune.common.utils.ExecutorThreadFactory;
import xyz.vopen.framework.neptune.common.utils.NetUtils;
import xyz.vopen.framework.neptune.core.persistence.Persistence;
import xyz.vopen.framework.neptune.rpc.RpcService;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link SchedulerService} Unified time task service that used for check the job status, clean the
 * expire log etc.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
public class SchedulerService {
  private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

  private static final String SCHEDULER_THREAD_POOL_NAME = "scheduler";
  private static final int MAX_BATCH = 10;
  private static final long DISPATCH_TIMEOUT_MS = 30000;

  private final @Nonnull Configuration configuration;
  private final @Nonnull RpcService rpcService;
  private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
  final Persistence persistence;
  private HashedWheelTimer hashedWheelTimer;

  public SchedulerService(
      final @Nonnull Configuration configuration,
      final @Nonnull RpcService rpcService,
      final @Nonnull Persistence persistence) {
    this.configuration = configuration;
    this.rpcService = rpcService;
    this.persistence = persistence;
    this.hashedWheelTimer = new HashedWheelTimer(5, 16, 0);
    this.scheduledThreadPoolExecutor =
        new ScheduledThreadPoolExecutor(3, new ExecutorThreadFactory(SCHEDULER_THREAD_POOL_NAME));
  }

  public void start() {
    scheduledThreadPoolExecutor.scheduleWithFixedDelay(
        new ServerStatusChecker(), 5000, 10000, TimeUnit.MILLISECONDS);
    scheduledThreadPoolExecutor.scheduleWithFixedDelay(
        new TaskAcquirer(), 2000, 5000, TimeUnit.MILLISECONDS);
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
              // 1. check the job which the status is WAITING_DISPATCH.

            });
  }

  // ===================== SCHEDULERS =====================

  /** Used to check the status of other server. */
  class ServerStatusChecker implements Runnable {
    @Override
    public void run() {
      Optional<List<ServerInfo>> serverInfos = persistence.getPersistenceAdapter().queryServers();
      if (!serverInfos.isPresent()) {
        return;
      }
    }
  }

  /** Used to obtain the task that belongs to current server. */
  class TaskAcquirer implements Runnable {
    @Override
    public void run() {
      Stopwatch stopwatch = Stopwatch.createStarted();
      Optional<List<JobInfo>> jobInfos =
          persistence.getPersistenceAdapter().findJobByAppId(NetUtils.getLocalAddress().toString());

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

  /** Used to clean expire log. */
  class LogCleaner implements Runnable {
    @Override
    public void run() {}
  }
}

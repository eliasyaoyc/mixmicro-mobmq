package xyz.vopen.framework.neptune.core.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.event.DispatchJobEvent;
import xyz.vopen.framework.neptune.common.event.ReDispatchJobEvent;
import xyz.vopen.framework.neptune.common.model.InstanceInfo;
import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.core.persistence.Persistence;
import xyz.vopen.framework.neptune.core.persistence.adapter.PersistenceAdapter;
import xyz.vopen.framework.neptune.rpc.FatalErrorHandler;
import xyz.vopen.framework.neptune.rpc.RpcService;

import javax.annotation.Nonnull;

/**
 * {@link StandaloneDispatcher}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class StandaloneDispatcher extends Dispatcher {
  private static final Logger LOG = LoggerFactory.getLogger(StandaloneDispatcher.class);

  private final @Nonnull PersistenceAdapter persistenceAdapter;

  StandaloneDispatcher(
      Configuration configuration,
      FatalErrorHandler fatalErrorHandler,
      RpcService rpcService,
      Persistence persistence) {
    super(configuration, StandaloneDispatcher.class.getSimpleName(), fatalErrorHandler, rpcService);
    this.persistenceAdapter = persistence.getPersistenceAdapter();
  }

  @Override
  public void dispatcher(DispatchJobEvent dispatchJobEvent) {
    dispatcher(
        dispatchJobEvent.getJobInfo(),
        dispatchJobEvent.getInstanceId(),
        dispatchJobEvent.getRunningTimes(),
        dispatchJobEvent.getInstanceParams(),
        dispatchJobEvent.getWorkFlowId());
  }

  @Override
  public void reDispatcher(ReDispatchJobEvent reDispatchJobEvent) {
    InstanceInfo instanceInfo =
        persistenceAdapter.findByInstanceId(reDispatchJobEvent.getInstanceId()).get();
    dispatcher(
        reDispatchJobEvent.getJobInfo(),
        reDispatchJobEvent.getInstanceId(),
        reDispatchJobEvent.getRunningTimes(),
        instanceInfo.getJobParams(),
        instanceInfo.getWorkFlowId());
  }

  /**
   * Dispatch tasks from Server to Worker (TaskTracker) running.
   *
   * @param jobInfo Task meta-information
   * @param instanceId Task instance ID
   * @param runningTimes Running times.
   * @param jobParams Instance running parameters, API triggering method dedicated
   * @param workFlowId Workflow task instance ID, dedicated for workflow task
   */
  private void dispatcher(
      JobInfo jobInfo, long instanceId, long runningTimes, String jobParams, long workFlowId) {
    LOG.debug(
        "[StandaloneDispatcher] start dispatch job: {} to worker: {} ",
        jobInfo.getId(),
        jobInfo.getDesignatedWorkers());
  }
}

package xyz.vopen.framework.scheduler.common.enums;

/**
 * {@link ExecuteState}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
public enum ExecuteState {
  CREATED,
  SCHEDULED,
  DEPLOYED,
  RUNNING,
  FAILED,
  FINISHED,
  CANCELING,
  CANCELED
}

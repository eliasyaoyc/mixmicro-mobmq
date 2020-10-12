package xyz.vopen.framework.neptune.common.enums;

/**
 * {@link ExecuteStatus}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
public enum ExecuteStatus {
  CREATED,
  SCHEDULED,
  DEPLOYED,
  RUNNING,
  FAILED,
  FINISHED,
  CANCELING,
  CANCELED
}

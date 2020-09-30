package xyz.vopen.framework.scheduler.common.enums;

/**
 * {@link JobState}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
public enum JobState {
  CREATED,
  RUNNING,
  FINISHED,
  CANCELLING,
  CANCELED,
  SUSPENDED,
  RESTARTING,
  FAILING,
  FAILED
}

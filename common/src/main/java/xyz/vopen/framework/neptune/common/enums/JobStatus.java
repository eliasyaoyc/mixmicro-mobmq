package xyz.vopen.framework.neptune.common.enums;

/**
 * {@link JobStatus} Possible states of a job once it has been accepted by the job manager.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public enum JobStatus {
  NEW(0),
  RUNNING(1),
  STOP(2);

  private int status;

  JobStatus(int status) {
    this.status = status;
  }

  public static JobStatus of(int status) {
    for (JobStatus value : values()) {
      if (value.getStatus() == status) {
        return value;
      }
    }
    throw new IllegalArgumentException("JobStatus has not support this status: " + status);
  }

  public int getStatus() {
    return status;
  }
}

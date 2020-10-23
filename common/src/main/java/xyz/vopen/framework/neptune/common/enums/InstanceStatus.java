package xyz.vopen.framework.neptune.common.enums;

/**
 * {@link InstanceStatus}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/23
 */
public enum InstanceStatus {
  WAITING_DISPATCH(1, "wait for dispatch"),
  WAITING_WORKER_RECEIVE(2, "wait for worker received"),
  RUNNING(3, "running"),
  FAILED(4, "failed"),
  SUCCEED(5, "success"),
  CANCELED(9, "cancel"),
  STOPPED(10, "manual stop");

  private int status;
  private String description;

  InstanceStatus(int status, String description) {
    this.status = status;
    this.description = description;
  }

  public static InstanceStatus of(int status) {
    for (InstanceStatus value : values()) {
      if (value.status == status) {
        return value;
      }
    }
    throw new IllegalArgumentException("InstanceStatus has not support this status: " + status);
  }

  // =====================  GETTER  =====================

  public int getStatus() {
    return status;
  }

  public String getDescription() {
    return description;
  }
}

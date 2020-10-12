package xyz.vopen.framework.neptune.common.enums;

/**
 * {@link ApplicationStatus} The status of application.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
public enum ApplicationStatus {
  /** Application finished successfully */
  SUCCEEDED(0),

  /** Application encountered an unrecoverable failure or error */
  FAILED(1443),

  /** Application was canceled or killed on request */
  CANCELED(0),

  /** Application status is not known */
  UNKNOWN(1445);

  /** The associated process exit code. */
  private final int processExitCode;

  private ApplicationStatus(int exitCode) {
    this.processExitCode = exitCode;
  }


  /**
   * Gets the process exit code associated with this status
   * @return The associated process exit code.
   */
  public int processExitCode() {
    return processExitCode;
  }

  /**
   * Derives the ApplicationStatus that should be used for a job that resulted in the given
   * job status. If the job is not yet in a globally terminal state, this method returns
   * {@link #UNKNOWN}.
   */
  public static ApplicationStatus fromJobStatus(JobStatus jobStatus) {
    if (jobStatus == null) {
      return UNKNOWN;
    }
    else {
      switch (jobStatus) {
        case FAILED:
          return FAILED;
        case CANCELED:
          return CANCELED;
        case FINISHED:
          return SUCCEEDED;

        default:
          return UNKNOWN;
      }
    }
  }
}

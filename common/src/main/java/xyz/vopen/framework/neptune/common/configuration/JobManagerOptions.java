package xyz.vopen.framework.neptune.common.configuration;

import static xyz.vopen.framework.neptune.common.configuration.ConfigOptions.key;

/** Configuration options for the JobManager. */
public class JobManagerOptions {

  private JobManagerOptions() {
    throw new IllegalAccessError();
  }

  public static final ConfigOption<String> NAME =
      key("jobmanager.name")
          .noDefaultValue()
          .withDescription("The config parameter defining the jobmanager name");
  /**
   * The config parameter defining the network address to connect to for communication with the job
   * manager.
   *
   * <p>This value is only interpreted in setups where a single JobManager with static name or
   * address exists (simple standalone setups, or container setups with dynamic service name
   * resolution). It is not used in many high-availability setups, when a leader-election service
   * (like ZooKeeper) is used to elect and discover the JobManager leader from potentially multiple
   * standby JobManagers.
   */
  public static final ConfigOption<String> ADDRESS =
      key("jobmanager.rpc.address")
          .noDefaultValue()
          .withDescription(
              "The config parameter defining the network address to connect to"
                  + " for communication with the job manager."
                  + " This value is only interpreted in setups where a single JobManager with static"
                  + " name or address exists (simple standalone setups, or container setups with dynamic"
                  + " service name resolution). It is not used in many high-availability setups, when a"
                  + " leader-election service (like ZooKeeper) is used to elect and discover the JobManager"
                  + " leader from potentially multiple standby JobManagers.");

  /** The local address of the network interface that the job manager binds to. */
  public static final ConfigOption<String> BIND_HOST =
      key("jobmanager.bind-host")
          .stringType()
          .noDefaultValue()
          .withDescription(
              "The local address of the network interface that the job manager binds to. If not"
                  + " configured, '0.0.0.0' will be used.");

  /**
   * The config parameter defining the network port to connect to for communication with the job
   * manager.
   *
   * <p>Like {@link JobManagerOptions#ADDRESS}, this value is only interpreted in setups where a
   * single JobManager with static name/address and port exists (simple standalone setups, or
   * container setups with dynamic service name resolution). This config option is not used in many
   * high-availability setups, when a leader-election service (like ZooKeeper) is used to elect and
   * discover the JobManager leader from potentially multiple standby JobManagers.
   */
  public static final ConfigOption<Integer> PORT =
      key("jobmanager.rpc.port")
          .defaultValue(6123)
          .withDescription(
              "The config parameter defining the network port to connect to"
                  + " for communication with the job manager."
                  + " Like "
                  + ADDRESS.key()
                  + ", this value is only interpreted in setups where"
                  + " a single JobManager with static name/address and port exists (simple standalone setups,"
                  + " or container setups with dynamic service name resolution)."
                  + " This config option is not used in many high-availability setups, when a"
                  + " leader-election service (like ZooKeeper) is used to elect and discover the JobManager"
                  + " leader from potentially multiple standby JobManagers.");

  /** The local port that the job manager binds to. */
  public static final ConfigOption<Integer> RPC_BIND_PORT =
      key("jobmanager.rpc.bind-port")
          .intType()
          .noDefaultValue()
          .withDescription(
              "The local RPC port that the JobManager binds to. If not configured, the external port"
                  + " (configured by '"
                  + PORT.key()
                  + "') will be used.");

  private static final String JVM_OVERHEAD_DESCRIPTION =
      "This is off-heap memory reserved for JVM "
          + "overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct "
          + "memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size "
          + "of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the "
          + "derived size is less or greater than the configured min or max size, the min or max size will be used. The "
          + "exact size of JVM Overhead can be explicitly specified by setting the min and max size to the same value.";

  /** The maximum number of prior execution attempts kept in history. */
  public static final ConfigOption<Integer> MAX_ATTEMPTS_HISTORY_SIZE =
      key("jobmanager.execution.attempts-history-size")
          .defaultValue(16)
          .withDeprecatedKeys("job-manager.max-attempts-history-size")
          .withDescription("The maximum number of prior execution attempts kept in history.");

  /**
   * This option specifies the failover strategy, i.e. how the job computation recovers from task
   * failures.
   */
  public static final ConfigOption<String> EXECUTION_FAILOVER_STRATEGY =
      key("jobmanager.execution.failover-strategy")
          .stringType()
          .defaultValue("region")
          .withDescription(
              Description.builder()
                  .text(
                      "This option specifies how the job computation recovers from task failures. "
                          + "Accepted values are:")
                  .build());

  /** The location where the JobManager stores the archives of completed jobs. */
  public static final ConfigOption<String> ARCHIVE_DIR =
      key("jobmanager.archive.fs.dir")
          .noDefaultValue()
          .withDescription("Dictionary for JobManager to store the archives of completed jobs.");

  /** The job store cache size in bytes which is used to keep completed jobs in memory. */
  public static final ConfigOption<Long> JOB_STORE_CACHE_SIZE =
      key("jobstore.cache-size")
          .defaultValue(50L * 1024L * 1024L)
          .withDescription(
              "The job store cache size in bytes which is used to keep completed jobs in memory.");

  /** The time in seconds after which a completed job expires and is purged from the job store. */
  public static final ConfigOption<Long> JOB_STORE_EXPIRATION_TIME =
      key("jobstore.expiration-time")
          .defaultValue(60L * 60L)
          .withDescription(
              "The time in seconds after which a completed job expires and is purged from the job store.");

  /** The max number of completed jobs that can be kept in the job store. */
  public static final ConfigOption<Integer> JOB_STORE_MAX_CAPACITY =
      key("jobstore.max-capacity")
          .defaultValue(Integer.MAX_VALUE)
          .withDescription("The max number of completed jobs that can be kept in the job store.");

  /** The timeout in milliseconds for requesting a slot from Slot Pool. */
  public static final ConfigOption<Long> SLOT_REQUEST_TIMEOUT =
      key("slot.request.timeout")
          .defaultValue(5L * 60L * 1000L)
          .withDescription("The timeout in milliseconds for requesting a slot from Slot Pool.");

  /** The timeout in milliseconds for a idle slot in Slot Pool. */
  //  public static final ConfigOption<Long> SLOT_IDLE_TIMEOUT =
  //      key("slot.idle.timeout")
  //          // default matches heartbeat.timeout so that sticky allocation is not lost on timeouts
  // for
  //          // local recovery
  //          .defaultValue(HeartbeatManagerOptions.HEARTBEAT_TIMEOUT.defaultValue())
  //          .withDescription("The timeout in milliseconds for a idle slot in Slot Pool.");

  /** Config parameter determining the scheduler implementation. */
  public static final ConfigOption<String> SCHEDULER =
      key("jobmanager.scheduler")
          .stringType()
          .defaultValue("ng")
          .withDescription(
              Description.builder()
                  .text(
                      "Determines which scheduler implementation is used to schedule tasks. Accepted values are:")
                  .build());
  /**
   * Config parameter controlling whether partitions should already be released during the job
   * execution.
   */
  public static final ConfigOption<Boolean> PARTITION_RELEASE_DURING_JOB_EXECUTION =
      key("jobmanager.partition.release-during-job-execution")
          .defaultValue(true)
          .withDescription(
              "Controls whether partitions should already be released during the job execution.");
}

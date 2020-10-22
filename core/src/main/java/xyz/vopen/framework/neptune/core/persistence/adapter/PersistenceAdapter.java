package xyz.vopen.framework.neptune.core.persistence.adapter;

import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.common.model.ServerInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * {@link PersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public interface PersistenceAdapter {

  /**
   * Returns the specifies server message through specifies the server name, allows null.
   *
   * @param serverName of the server.
   * @return The specifies server message.
   */
  Optional<ServerInfo> queryServerByName(String serverName);

  /**
   * Returns all servers message,allows null.
   *
   * @return The all servers message.
   */
  Optional<List<ServerInfo>> queryServers();

  /**
   * Save the server message.
   *
   * @param serverInfo {@link ServerInfo} instance.
   * @return
   */
  void saveServerInfo(@Nonnull ServerInfo serverInfo);

  /**
   * Returns the specify job through specify the job id.
   *
   * @param jobId of job.
   * @return Job collection.
   */
  Optional<JobInfo> findJobById(String jobId);

  /**
   * Returns the specify job collection under server.
   *
   * @param appId Represent the server id.
   * @return Job collection.
   */
  Optional<List<JobInfo>> findJobByAppId(String appId);

  /**
   * Returns the specify job collection through appId and job name.
   *
   * @param appId server id.
   * @param name of job.
   * @return Job collection.
   */
  Optional<List<JobInfo>> findJobByAppIdAndName(String appId, String name);

  /**
   * Save the job message.
   *
   * @param jobInfo {@link JobInfo} instance.
   * @return
   */
  void saveJobInfo(JobInfo jobInfo);

  /**
   * Update the job message.
   *
   * @param jobInfo {@link JobInfo instance.}
   * @return
   */
  void updateJobInfo(JobInfo jobInfo);
}

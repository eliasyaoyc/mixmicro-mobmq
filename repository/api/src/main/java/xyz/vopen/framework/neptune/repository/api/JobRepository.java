package xyz.vopen.framework.neptune.repository.api;

import xyz.vopen.framework.neptune.common.model.JobInfo;

import java.util.List;
import java.util.Optional;

/**
 * {@link JobRepository} The repository interface for {@link JobInfo}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/20
 */
public interface JobRepository extends BaseRepository {

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

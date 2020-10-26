package xyz.vopen.framework.neptune.core.persistence.adapter;

import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.model.InstanceInfo;
import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.common.model.ServerInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * {@link MongoPersistenceAdapter}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class MongoPersistenceAdapter implements PersistenceAdapter {

  private MongoPersistenceAdapter(Configuration configuration) {}

  public static MongoPersistenceAdapter create(final @Nonnull Configuration configuration) {
    return new MongoPersistenceAdapter(configuration);
  }

  @Override
  public Optional<ServerInfo> queryServerByName(String serverName) {
    return Optional.empty();
  }

  @Override
  public Optional<List<ServerInfo>> queryServers() {
    return Optional.empty();
  }

  @Override
  public void saveServerInfo(@Nonnull ServerInfo serverInfo) {}

  @Override
  public Optional<JobInfo> findJobById(long jobId) {
    return Optional.empty();
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppId(long appId) {
    return Optional.empty();
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppIdAndStatus(long appId, int status) {
    return Optional.empty();
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppIdAndName(long appId, String name) {
    return Optional.empty();
  }

  @Override
  public void saveJobInfo(JobInfo jobInfo) {}

  @Override
  public void updateJobInfo(JobInfo jobInfo) {}

  @Override
  public long countByJobIdAndStatus(long jobId, List<Integer> status) {
    return 0;
  }

  @Override
  public Optional<InstanceInfo> findByInstanceId(long instanceId) {
    return Optional.empty();
  }

  @Override
  public Optional<List<InstanceInfo>> findByJobIdAndStatus(long jobId, List<Integer> status) {
    return Optional.empty();
  }

  @Override
  public Optional<List<InstanceInfo>> findInstancesByAppId(long appId) {
    return Optional.empty();
  }

  @Override
  public Optional<List<InstanceInfo>> findInstancesByAppIdAndStatus(long appId, int status) {
    return Optional.empty();
  }

  @Override
  public void saveInstanceInfo(InstanceInfo instanceInfo) {

  }

  @Override
  public void updateInstanceInfo(InstanceInfo instanceInfo) {

  }

  @Override
  public void deleteInstance(Long instanceIds) {

  }
}

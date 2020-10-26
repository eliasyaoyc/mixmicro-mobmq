package xyz.vopen.framework.neptune.core.persistence.adapter;

import com.google.common.base.Preconditions;
import xyz.vopen.framework.neptune.common.configuration.Configuration;
import xyz.vopen.framework.neptune.common.model.InstanceInfo;
import xyz.vopen.framework.neptune.common.model.JobInfo;
import xyz.vopen.framework.neptune.common.model.ServerInfo;
import xyz.vopen.framework.repository.mysql.MysqlRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * {@link DefaultPersistenceAdapter} Adapter {@link MysqlRepository}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/13
 */
public class DefaultPersistenceAdapter implements PersistenceAdapter {

  private final @Nonnull MysqlRepository mysqlRepository;

  private DefaultPersistenceAdapter(final Configuration configuration) {
    Preconditions.checkNotNull(configuration);
    this.mysqlRepository = MysqlRepository.createFromConfiguration(configuration);
  }

  public static DefaultPersistenceAdapter create(final Configuration configuration) {
    return new DefaultPersistenceAdapter(configuration);
  }

  @Override
  public Optional<ServerInfo> queryServerByName(@Nonnull String serverName) {
    Optional<ServerInfo> serverInfo = mysqlRepository.queryServerByName(serverName);
    return serverInfo;
  }

  @Override
  public Optional<List<ServerInfo>> queryServers() {
    Optional<List<ServerInfo>> serverInfos = mysqlRepository.queryServers();
    return serverInfos;
  }

  @Override
  public void saveServerInfo(@Nonnull ServerInfo serverInfo) {
    mysqlRepository.saveServerInfo(serverInfo);
  }

  @Override
  public Optional<JobInfo> findJobById(long jobId) {
    return mysqlRepository.findJobById(jobId);
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppId(long appId) {
    return mysqlRepository.findJobByAppId(appId);
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppIdAndStatus(long appId, int status) {
    return mysqlRepository.findJobByAppIdAndStatus(appId,status);
  }

  @Override
  public Optional<List<JobInfo>> findJobByAppIdAndName(long appId, String name) {
    return mysqlRepository.findJobByAppIdAndName(appId, name);
  }

  @Override
  public void saveJobInfo(JobInfo jobInfo) {
    mysqlRepository.saveJobInfo(jobInfo);
  }

  @Override
  public void updateJobInfo(JobInfo jobInfo) {
    mysqlRepository.updateJobInfo(jobInfo);
  }

  @Override
  public long countByJobIdAndStatus(long jobId, List<Integer> status) {
    return mysqlRepository.countByJobIdAndStatus(jobId, status);
  }

  @Override
  public Optional<InstanceInfo> findByInstanceId(long instanceId) {
    return mysqlRepository.findByInstanceId(instanceId);
  }

  @Override
  public Optional<List<InstanceInfo>> findByJobIdAndStatus(long jobId, List<Integer> status) {
    return mysqlRepository.findByJobIdAndStatus(jobId, status);
  }

  @Override
  public Optional<List<InstanceInfo>> findInstancesByAppId(long appId) {
    return mysqlRepository.findInstancesByAppId(appId);
  }

  @Override
  public Optional<List<InstanceInfo>> findInstancesByAppIdAndStatus(long appId, int status) {
    return mysqlRepository.findInstancesByAppIdAndStatus(appId,status);
  }

  @Override
  public void saveInstanceInfo(InstanceInfo instanceInfo) {
    mysqlRepository.saveInstanceInfo(instanceInfo);
  }

  @Override
  public void updateInstanceInfo(InstanceInfo instanceInfo) {
    mysqlRepository.updateInstanceInfo(instanceInfo);
  }

  @Override
  public void deleteInstance(Long instanceIds) {
    mysqlRepository.deleteInstance(instanceIds);
  }
}

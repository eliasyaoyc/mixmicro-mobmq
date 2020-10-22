package xyz.vopen.framework.neptune.repository.api;

import xyz.vopen.framework.neptune.common.model.InstanceInfo;

import java.util.List;
import java.util.Optional;

/**
 * {@link InstanceInfoRepository}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
public interface InstanceInfoRepository {

  long countByJobIdAndStatus(long jobId, List<Integer> status);

  Optional<List<InstanceInfo>> findByJobIdAndStatus(long jobId, List<Integer> status);

  void saveInstanceInfo(InstanceInfo instanceInfo);

  void updateInstanceInfo(InstanceInfo instanceInfo);

  void deleteInstance(Long instanceIds);
}

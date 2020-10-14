package xyz.vopen.framework.neptune.core.dispatcher;

import xyz.vopen.framework.neptune.common.configuration.Configuration;

/**
 * {@link DispatcherServicesFactory} DispatcherServices factory.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public enum DispatcherServicesFactory {
  INSTANCE;

  /**
   * Create {@link DispatcherServices}.
   *
   * @param configuration
   * @return
   */
  public DispatcherServices create(final Configuration configuration) {
    return new DispatcherServices(configuration);
  }
}

package xyz.vopen.framework.neptune.common.configuration;

import java.util.Optional;

/**
 * {@link ReadableConfig} Read access to a configuration object. Allows reading values described
 * with meta information included in {@link ConfigOption}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public interface ReadableConfig {
  /**
   * Reads a value using the metada included in {@link ConfigOption}. Returns the {@link
   * ConfigOption#defaultValue()} if value key not present in the configuration.
   *
   * @param option metadata of the option to read
   * @param <T> type of the value to read
   * @return read value or {@link ConfigOption#defaultValue()} if not found
   * @see #getOptional(ConfigOption)
   */
  <T> T get(ConfigOption<T> option);

  /**
   * Reads a value using the metada included in {@link ConfigOption}. In contrast to {@link
   * #get(ConfigOption)} returns {@link Optional#empty()} if value not present.
   *
   * @param option metadata of the option to read
   * @param <T> type of the value to read
   * @return read value or {@link Optional#empty()} if not found
   * @see #get(ConfigOption)
   */
  <T> Optional<T> getOptional(ConfigOption<T> option);
}

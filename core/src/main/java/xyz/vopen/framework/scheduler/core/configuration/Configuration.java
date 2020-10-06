package xyz.vopen.framework.scheduler.core.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * {@link Configuration} Lightweight configuration object which stores key/value pairs.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class Configuration<T> {
  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  private static final long serialVersionUID = 1L;
  private static final byte TYPE_STRING = 0;
  private static final byte TYPE_INT = 1;
  private static final byte TYPE_LONG = 2;
  private static final byte TYPE_BOOLEAN = 3;
  private static final byte TYPE_FLOAT = 4;
  private static final byte TYPE_DOUBLE = 5;
  private static final byte TYPE_BYTES = 6;

  /** Store the concrete key/value pairs of this configuration object. */
  protected final HashMap<String, Object> confData;

  /** Creates a new empty configuration. */
  public Configuration() {
    this.confData = new HashMap<String, Object>();
  }

  /**
   * Creates a new configuration with the copy of the given configuration.
   *
   * @param other The configuration to copy the entries from.
   */
  public Configuration(Configuration other) {
    this.confData = new HashMap<String, Object>(other.confData);
  }

  /**
   * Returns the default value, or null, if there is no default value.
   *
   * @return The default value, or null.
   */
  public T defaultValue() {
    return null;
    //    return defaultValue;
  }
  /**
   * Returns the value associated with the given config option as a string.
   *
   * @param configOption The configuration option
   * @return the (default) value associated with the given config option
   */
//  public String getString(ConfigOption<String> configOption) {
//    return getOptional(configOption).orElseGet(configOption::defaultValue);
//  }
}

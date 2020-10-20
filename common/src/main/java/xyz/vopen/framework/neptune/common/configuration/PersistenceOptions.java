package xyz.vopen.framework.neptune.common.configuration;

import static xyz.vopen.framework.neptune.common.configuration.ConfigOptions.key;

/**
 * {@link PersistenceOptions} Configuration for persistence.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/20
 */
public class PersistenceOptions {
  private PersistenceOptions() {
    throw new AssertionError();
  }

  // =====================   MYSQL Configuration  =====================
  public static final ConfigOption<String> MYSQL_ADDRESS =
      key("mysql.address")
          .defaultValue("localhost")
          .withDescription("The config parameter defining the mysql address to connect to");

  public static final ConfigOption<Integer> MYSQL_PORT =
      key("mysql.port")
          .defaultValue(3306)
          .withDescription("The config parameter defining the mysql port to connect to");

  public static final ConfigOption<String> MYSQL_USER =
      key("mysql.user")
          .defaultValue("root")
          .withDescription("The config parameter defining the mysql user to connect to");

  public static final ConfigOption<String> MYSQL_PASSWORD =
      key("mysql.password")
          .noDefaultValue()
          .withDescription(
              "The config parameter defining the mysql user corresponding password to connect to");

  public static final ConfigOption<String> MYSQL_DATABASE =
      key("mysql.database")
          .defaultValue("neptune")
          .withDescription("The config parameter defining the database to connect to");

  public static final ConfigOption<String> MYSQL_URL =
      key("mysql.url")
          .noDefaultValue()
          .withDescription("The config parameter defining the mysql url to connect to");

  // =====================  MONGO Configuration  =====================
}

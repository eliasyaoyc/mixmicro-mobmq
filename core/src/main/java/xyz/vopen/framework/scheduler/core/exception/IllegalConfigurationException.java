package xyz.vopen.framework.scheduler.core.exception;

import javax.validation.constraints.NotNull;

/**
 * An {@link IllegalConfigurationException} is thrown when the values in a given {@link
 * xyz.vopen.framework.scheduler.core.configuration.Configuration} are not valid. This may refer to
 * the Scheduler configuration with which the framework is started, or a Configuration passed
 * internally between components.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class IllegalConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 731344753923092005L;

  public IllegalConfigurationException() {
    super();
  }

  public IllegalConfigurationException(@NotNull String message) {
    super(message);
  }

  public IllegalConfigurationException(String format, Object... arguments) {
    super(String.format(format, arguments));
  }

  public IllegalConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}

package xyz.vopen.framework.neptune.common.exceptions;

import javax.annotation.Nullable;

/**
 * {@link IllegalConfigurationException} An {@code IllegalConfigurationException} is thrown when the
 * values in a given Configuration are not valid. This may refer to the Neptune configuration with
 * which the framework is started, or a Configuration passed internally between components.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class IllegalConfigurationException extends NeptuneRuntimeException {
  private static final long serialVersionUID = -445015905958131297L;

  public IllegalConfigurationException() {
    super();
  }

  public IllegalConfigurationException(@Nullable String message) {
    super(message);
  }

  public IllegalConfigurationException(@Nullable Throwable cause) {
    super(cause);
  }

  public IllegalConfigurationException(String format, Object... arguments) {
    super(String.format(format, arguments));
  }

  public IllegalConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}

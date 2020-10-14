package xyz.vopen.framework.neptune.common.exceptions;

import javax.annotation.Nonnull;

/**
 * {@link NeptuneParseException} Exception which indicates that the parsing of command line
 * arguments failed.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class NeptuneParseException extends NeptuneException {
  private static final long serialVersionUID = 6365106215462664781L;

  public NeptuneParseException() {
    super();
  }

  public NeptuneParseException(@Nonnull String message) {
    super(message);
  }

  public NeptuneParseException(@Nonnull Throwable cause) {
    super(cause);
  }

  public NeptuneParseException(String message, Throwable cause) {
    super(message, cause);
  }
}

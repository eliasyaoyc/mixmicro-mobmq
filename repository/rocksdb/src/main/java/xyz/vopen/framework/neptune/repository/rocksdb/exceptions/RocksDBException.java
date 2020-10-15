package xyz.vopen.framework.neptune.repository.rocksdb.exceptions;

import xyz.vopen.framework.neptune.common.exceptions.NeptuneException;

import javax.annotation.Nonnull;

/**
 * {@link RocksDBException}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/15
 */
public class RocksDBException extends NeptuneException {
  private static final long serialVersionUID = 4693003026585681766L;

  public RocksDBException() {
    super();
  }

  public RocksDBException(@Nonnull String message) {
    super(message);
  }

  public RocksDBException(@Nonnull Throwable cause) {
    super(cause);
  }

  public RocksDBException(String message, Throwable cause) {
    super(message, cause);
  }
}

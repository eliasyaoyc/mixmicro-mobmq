package xyz.vopen.framework.neptune.core.configuration;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * {@link MemorySize}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class MemorySize implements Serializable, Comparable<MemorySize> {
  private static final long serialVersionUID = 1781212653665906198L;

  /** The memory size, in bytes. */
  private final long bytes;

  public static MemorySize parse(String toString) {
    return null;
  }

  public MemorySize(long bytes) {
    Preconditions.checkArgument(bytes >= 0, "bytes must be >= 0");
    this.bytes = bytes;
  }

  public static MemorySize ofMebiBytes(long mebiBytes) {
    return new MemorySize(mebiBytes << 20);
  }

  @Override
  public int compareTo(MemorySize o) {
    return 0;
  }
}

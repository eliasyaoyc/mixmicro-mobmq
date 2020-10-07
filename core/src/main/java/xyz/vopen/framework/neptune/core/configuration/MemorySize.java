package xyz.vopen.framework.neptune.core.configuration;

import java.io.Serializable;

/**
 * {@link MemorySize}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class MemorySize implements Serializable, Comparable<MemorySize> {
  private static final long serialVersionUID = 1781212653665906198L;

  public static MemorySize parse(String toString) {
    return null;
  }

  @Override
  public int compareTo(MemorySize o) {
    return 0;
  }
}

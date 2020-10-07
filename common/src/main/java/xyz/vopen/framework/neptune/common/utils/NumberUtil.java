package xyz.vopen.framework.neptune.common.utils;

/**
 * {@link NumberUtil}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class NumberUtil {
  private static final int MAXIMUM_CAPACITY = 1 << 30;

  /**
   * Format the size to 2 N times.
   *
   * @param size init size.
   * @return 2 N times.
   */
  public static int formatSize(int size) {
    int n = size - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n > MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
  }
}

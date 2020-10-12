package xyz.vopen.framework.neptune.common.configuration;

/**
 * {@link FallbackKey} A key with FallbackKeys will fall back to the FallbackKeys if it itself is
 * not configured.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class FallbackKey {

  private final String key;
  private final boolean isDeprecated;

  private FallbackKey(String key, boolean isDeprecated) {
    this.key = key;
    this.isDeprecated = isDeprecated;
  }

  public String getKey() {
    return this.key;
  }

  public boolean isDeprecated() {
    return this.isDeprecated;
  }

  static FallbackKey createFallbackKey(String key) {
    return new FallbackKey(key, false);
  }

  static FallbackKey createDeprecatedKey(String key) {
    return new FallbackKey(key, true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && o.getClass() == FallbackKey.class) {
      FallbackKey that = (FallbackKey) o;
      return this.key.equals(that.key) && (this.isDeprecated == that.isDeprecated);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return 31 * key.hashCode() + (isDeprecated ? 1 : 0);
  }

  @Override
  public String toString() {
    return String.format("{key=%s, isDeprecated=%s}", key, isDeprecated);
  }
}

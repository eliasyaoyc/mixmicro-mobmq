package xyz.vopen.framework.neptune.common.utils;

import com.google.common.base.Preconditions;
import xyz.vopen.framework.neptune.common.annoations.Internal;

import java.io.*;
import java.util.Arrays;

/**
 * {@link SerializedValue} This class is used to transfer (via serialization) objects whose classes
 * are not available in the system class loader. When those objects are deserialized without access
 * to their special class loader, the deserialization fails with a {@code ClassNotFoundException}.
 *
 * <p>To work around that issue, the SerializedValue serialized data immediately into a byte array.
 * When send through RPC or another service that uses serialization, only the byte array is
 * transferred. The object is deserialized later (upon access) and requires the accessor to provide
 * the corresponding class loader.
 *
 * @param <T> The type of the value held.
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
@Internal
public class SerializedValue<T> implements Serializable {
  private static final long serialVersionUID = 161775940257247262L;

  /** The serialized data. */
  private final byte[] serializedData;

  private SerializedValue(byte[] serializedData) {
    Preconditions.checkNotNull(serializedData, "Serialized data");
    this.serializedData = serializedData;
  }

  public SerializedValue(T value) throws IOException {
    this.serializedData = value == null ? null : InstantiationUtil.serializeObject(value);
  }

  public T deserializeValue(ClassLoader loader) throws IOException, ClassNotFoundException {
    Preconditions.checkNotNull(loader, "No classloader has been passed");
    return serializedData == null
        ? null
        : (T) InstantiationUtil.deserializeObject(serializedData, loader);
  }

  /**
   * Returns the serialized value or <code>null</code> if no value is set.
   *
   * @return Serialized data.
   */
  public byte[] getByteArray() {
    return this.serializedData;
  }

  public static <T> SerializedValue<T> fromBytes(byte[] serializedData) {
    return new SerializedValue<>(serializedData);
  }

  @Override
  public int hashCode() {
    return serializedData == null ? 0 : Arrays.hashCode(serializedData);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SerializedValue) {
      SerializedValue<?> other = (SerializedValue<?>) obj;
      return this.serializedData == null
          ? other.serializedData == null
          : (other.serializedData != null
              && Arrays.equals(this.serializedData, other.serializedData));
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "SerializedValue";
  }
}

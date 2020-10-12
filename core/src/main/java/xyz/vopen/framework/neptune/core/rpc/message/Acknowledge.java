package xyz.vopen.framework.neptune.core.rpc.message;

import java.io.Serializable;

/**
 * {@link Acknowledge} A generic acknowledgement message.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/12
 */
public class Acknowledge implements Serializable {
  private static final long serialVersionUID = -5969618006622193359L;

  private Acknowledge() {}

  // ================== Singleton =====================
  public static Acknowledge getInstance() {
    return AcknowledgeLazyHolder.INSTANCE;
  }

  static class AcknowledgeLazyHolder {
    static final Acknowledge INSTANCE = new Acknowledge();
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == Acknowledge.class;
  }

  @Override
  public int hashCode() {
    return 41;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  /**
   * Read resolve to preserve the singleton object property. (per best practices, this should have
   * visibility 'protected')
   */
  protected Object readResolve() throws java.io.ObjectStreamException {
    return getInstance();
  }
}

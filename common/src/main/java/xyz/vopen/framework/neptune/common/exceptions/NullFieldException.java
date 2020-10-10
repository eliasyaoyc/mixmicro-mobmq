package xyz.vopen.framework.neptune.common.exceptions;

/**
 * {@link NullFieldException} An exception specifying that a required field was not set in a record,
 * i.e. was <code>null</code>
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class NullFieldException extends NeptuneRuntimeException {
  private static final long serialVersionUID = -1331996698318815301L;

  private final int fieldPos;


  /** Constructs an {@code NullFieldException} with {@code null} as its error detail message. */
  public NullFieldException() {
    super();
    this.fieldPos = -1;
  }

  /**
   * Constructs an {@code NullFieldException} with the specified detail message.
   *
   * @param message The detail message.
   */
  public NullFieldException(String message) {
    super(message);
    this.fieldPos = -1;
  }

  /**
   * Constructs an {@code NullFieldException} with a default message, referring to given field
   * number as the null field.
   *
   * @param fieldIdx The index of the field that was null, but expected to hold a value.
   */
  public NullFieldException(int fieldIdx) {
    super("Field " + fieldIdx + " is null, but expected to hold a value.");
    this.fieldPos = fieldIdx;
  }

  /**
   * Constructs an {@code NullFieldException} with a default message, referring to given field
   * number as the null field and a cause (Throwable)
   *
   * @param fieldIdx The index of the field that was null, but expected to hold a value.
   * @param cause Pass the root cause of the error
   */
  public NullFieldException(int fieldIdx, Throwable cause) {
    super("Field " + fieldIdx + " is null, but expected to hold a value.", cause);
    this.fieldPos = fieldIdx;
  }

  /**
   * Gets the field number that was attempted to access. If the number is not set, this method
   * returns {@code -1}.
   *
   * @return The field number that was attempted to access, or {@code -1}, if not set.
   */
  public int getFieldPos() {
    return this.fieldPos;
  }
}

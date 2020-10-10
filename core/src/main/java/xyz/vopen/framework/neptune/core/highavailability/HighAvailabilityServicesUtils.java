package xyz.vopen.framework.neptune.core.highavailability;

/**
 * {@link HighAvailabilityServicesUtils} Utility class to instantiate {@link
 * HighAvailabilityServices} implementations.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class HighAvailabilityServicesUtils {

  /**
   * Enum specifying whether address resolution should be tried or not when creating the {@link
   * HighAvailabilityServices}.
   */
  public enum AddressResolution {
    TRY_ADDRESS_RESOLUTION,
    NO_ADDRESS_RESOLUTION
  }
}

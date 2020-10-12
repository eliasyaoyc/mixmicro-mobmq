package xyz.vopen.framework.neptune.common.utils;

/**
 * {@link HighAvailabilityServicesUtil} Utility class to instantiate HighAvailabilityServices
 * implementations.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/10
 */
public class HighAvailabilityServicesUtil {

  /**
   * Enum specifying whether address resolution should be tried or not when creating the
   * HighAvailabilityServices.
   */
  public enum AddressResolution {
    TRY_ADDRESS_RESOLUTION,
    NO_ADDRESS_RESOLUTION
  }
}

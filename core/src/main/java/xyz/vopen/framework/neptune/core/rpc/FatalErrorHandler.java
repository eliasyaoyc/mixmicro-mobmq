package xyz.vopen.framework.neptune.core.rpc;

/**
 * {@link FatalErrorHandler} Handler for fatal errors.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public interface FatalErrorHandler {

  /**
   * Being called when a fatal error occurs.
   *
   * <p>IMPORTANT: This call should never be blocking since it might be called from within the main
   * thread of an {@link RpcEndpoint}.
   *
   * @param exception cause.
   */
  void onFatalError(Throwable exception);
}

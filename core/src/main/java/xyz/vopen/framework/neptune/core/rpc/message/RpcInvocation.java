package xyz.vopen.framework.neptune.core.rpc.message;

import java.io.IOException;

/**
 * {@link RpcInvocation} Interface for rpc invocation messages. The interface allows to request all
 * necessary information to lookup a method and call it with the corresponding arguments.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public interface RpcInvocation {
  /**
   * Returns the method's name.
   *
   * @return Method name
   * @throws IOException if the rpc invocation message is a remote message and could not be
   *     deserialized
   * @throws ClassNotFoundException if the rpc invocation message is a remote message and contains
   *     serialized classes which cannot be found on the receiving side
   */
  String getMethodName() throws IOException, ClassNotFoundException;

  /**
   * Returns the method's parameter types
   *
   * @return Method's parameter types
   * @throws IOException if the rpc invocation message is a remote message and could not be
   *     deserialized
   * @throws ClassNotFoundException if the rpc invocation message is a remote message and contains
   *     serialized classes which cannot be found on the receiving side
   */
  Class<?>[] getParameterTypes() throws IOException, ClassNotFoundException;

  /**
   * Returns the arguments of the remote procedure call
   *
   * @return Arguments of the remote procedure call
   * @throws IOException if the rpc invocation message is a remote message and could not be
   *     deserialized
   * @throws ClassNotFoundException if the rpc invocation message is a remote message and contains
   *     serialized classes which cannot be found on the receiving side
   */
  Object[] getArgs() throws IOException, ClassNotFoundException;
}

package xyz.vopen.framework.neptune.core.rpc.message;

import java.io.IOException;

/**
 * {@link LocalRpcInvocation} Local rpc invocation message containing the remote procedure name, its
 * parameters types and the corresponding call arguments. This message will only be sent if the
 * communication is local and, thus, the message dose not have to be serialized.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/7
 */
public class LocalRpcInvocation implements RpcInvocation {
  private final String methodName;
  private final Class<?>[] parameterTypes;
  private final Object[] args;

  private transient String toString;

  public LocalRpcInvocation(String methodName, Class<?>[] parameterTypes, Object[] args) {
    this.methodName = methodName;
    this.parameterTypes = parameterTypes;
    this.args = args;

    this.toString = null;
  }

  @Override
  public String getMethodName() throws IOException, ClassNotFoundException {
    return this.methodName;
  }

  @Override
  public Class<?>[] getParameterTypes() throws IOException, ClassNotFoundException {
    return this.parameterTypes;
  }

  @Override
  public Object[] getArgs() throws IOException, ClassNotFoundException {
    return this.args;
  }

  @Override
  public String toString() {
    if (toString == null) {
      StringBuilder paramTypeStringBuilder = new StringBuilder(parameterTypes.length * 5);

      if (parameterTypes.length > 0) {
        paramTypeStringBuilder.append(parameterTypes[0].getSimpleName());

        for (int i = 1; i < parameterTypes.length; i++) {
          paramTypeStringBuilder.append(", ").append(parameterTypes[i].getSimpleName());
        }
      }

      toString = "LocalRpcInvocation(" + methodName + '(' + paramTypeStringBuilder + "))";
    }

    return toString;
  }
}

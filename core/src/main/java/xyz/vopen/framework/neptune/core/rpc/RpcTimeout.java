package xyz.vopen.framework.neptune.core.rpc;

import java.lang.annotation.*;

/**
 * {@link RpcTimeout} Annotation for {@link RpcGateway} methods to specify an additional timeout
 * parameter for the returned future to be completed. The rest of the provided parameters is passed
 * to the remote rpc server for the rpc.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/29
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcTimeout {

}

package xyz.vopen.framework.neptune.rpc.akka;

/**
 * {@link ControlMessages} Control message for {@link AkkaRpcActor}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public enum ControlMessages {
  START, // Start processing incoming message.
  STOP, // Stop processing messages and drop all newly incoming messages.
  TERMINATE, // Terminate the AkkaRpcActor.
}

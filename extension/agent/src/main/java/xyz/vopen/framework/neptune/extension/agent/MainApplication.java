package xyz.vopen.framework.neptune.extension.agent;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

/**
 * {@link MainApplication}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/22
 */
@Command(name = "NeptuneAgent", mixinStandardHelpOptions = true)
public class MainApplication implements Runnable {
  @Option(
      names = {"--app", "-a"},
      description = "agent's name",
      required = true)
  private String appName;

  @Option(
      names = {"--port", "-p"},
      description = "akka ActorSystem port,not recommended to change")
  private Integer port;

  @Option(
      names = {"--persistence", "-e"},
      description = "storage strategy")
  private String storeStrategy;

  @Option(
      names = {"--server", "-s"},
      description = "neptune server address",
      required = true)
  private String server;

  @Option(
      names = {"--length", "-l"},
      description = "request max length")
  private int length;

  public static void main(String[] args) {
    CommandLine commandLine = new CommandLine(new MainApplication());
    commandLine.execute(args);
  }

  @Override
  public void run() {

  }
}

package xyz.vopen.framework.neptune.common.configuration.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import xyz.vopen.framework.neptune.common.configuration.JobManagerOptions;

import javax.annotation.Nonnull;

/**
 * {@link NeptuneConfigurationParserFactory} Parser factory which generates a {@link
 * NeptuneConfiguration} from a given list of command line arguments.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class NeptuneConfigurationParserFactory
    implements ParserResultFactory<NeptuneConfiguration> {

  @Override
  public Options getOptions() {
    final Options options = new Options();
    options.addOption(
        Option.builder()
            .longOpt(JobManagerOptions.ADDRESS.key())
            .required(true)
            .hasArg(true)
            .argName("job manager rpc address")
            .build());
    options.addOption(
        Option.builder()
            .longOpt(JobManagerOptions.PORT.key())
            .required(false)
            .hasArg(true)
            .argName("job manager rpc port")
            .build());

    options.addOption(
        Option.builder()
            .longOpt(JobManagerOptions.BIND_HOST.key())
            .required(false)
            .hasArg(true)
            .argName("job manager bind host")
            .build());
    options.addOption(
        Option.builder()
            .longOpt(JobManagerOptions.RPC_BIND_PORT.key())
            .required(false)
            .hasArg(true)
            .argName("job manager rpc bin port")
            .build());
    return options;
  }

  @Override
  public NeptuneConfiguration createResult(@Nonnull CommandLine commandLine) {
    @Nonnull
    String jobManagerRpcAddress = commandLine.getOptionValue(JobManagerOptions.ADDRESS.key());
    String jobManagerRpcPort = commandLine.getOptionValue(JobManagerOptions.PORT.key());
    String jobManagerBindHost = commandLine.getOptionValue(JobManagerOptions.BIND_HOST.key());
    String jobManagerRpcBindPort =
        commandLine.getOptionValue(JobManagerOptions.RPC_BIND_PORT.key());
    return new NeptuneConfiguration(
        jobManagerRpcAddress, jobManagerRpcPort, jobManagerBindHost, jobManagerRpcBindPort);
  }
}

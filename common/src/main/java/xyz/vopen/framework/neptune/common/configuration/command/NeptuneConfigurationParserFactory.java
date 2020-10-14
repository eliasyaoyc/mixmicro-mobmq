package xyz.vopen.framework.neptune.common.configuration.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneParseException;

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

    return options;
  }

  @Override
  public NeptuneConfiguration createResult(@Nonnull CommandLine commandLine)
      throws NeptuneParseException {
    return new NeptuneConfiguration();
  }
}

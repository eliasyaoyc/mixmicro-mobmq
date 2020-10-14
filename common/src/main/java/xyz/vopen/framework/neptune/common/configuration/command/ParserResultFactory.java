package xyz.vopen.framework.neptune.common.configuration.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneParseException;

import javax.annotation.Nonnull;

/**
 * {@link ParserResultFactory} Parser result factory used by the {@link CommandLineParser}.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public interface ParserResultFactory<T> {

  /**
   * Returns all relevant {@link org.apache.commons.cli.Option} for parsing the command line
   * arguments.
   *
   * @return Options to use for the parsing.
   */
  Options getOptions();

  /**
   * Create the result of the command line argument parsing.
   *
   * @param commandLine to extract the options from.
   * @return Result of the parsing
   * @throws NeptuneParseException Thrown on failures while parsing command line arguments.
   */
  T createResult(@Nonnull CommandLine commandLine) throws NeptuneParseException;
}

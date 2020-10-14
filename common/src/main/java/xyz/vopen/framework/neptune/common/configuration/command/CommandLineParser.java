package xyz.vopen.framework.neptune.common.configuration.command;

import org.apache.commons.cli.*;
import xyz.vopen.framework.neptune.common.exceptions.NeptuneParseException;

import javax.annotation.Nonnull;

/**
 * {@link CommandLineParser} Command line parser which produces a result from the given command line
 * arguments.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/14
 */
public class CommandLineParser<T> {
  private final @Nonnull ParserResultFactory<T> parserResultFactory;

  public CommandLineParser(@Nonnull ParserResultFactory<T> parserResultFactory) {
    this.parserResultFactory = parserResultFactory;
  }

  public T parse(@Nonnull String[] args) throws NeptuneParseException {
    final DefaultParser defaultParser = new DefaultParser();
    final Options options = this.parserResultFactory.getOptions();

    final CommandLine commandLine;
    try {
      commandLine = defaultParser.parse(options, args, true);
    } catch (ParseException e) {
      throw new NeptuneParseException("Failed to parse the command line argument.", e);
    }
    return parserResultFactory.createResult(commandLine);
  }

  public void printHelp(@Nonnull String cmdLineSyntax) {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.setLeftPadding(5);
    helpFormatter.setWidth(80);
    helpFormatter.printHelp(cmdLineSyntax, parserResultFactory.getOptions(), true);
  }
}

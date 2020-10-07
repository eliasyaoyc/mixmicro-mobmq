package xyz.vopen.framework.neptune.bootstrap;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * {@link Bootstrap}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@SpringBootApplication
public class Bootstrap {

  /**
   * Startup Scheduler Core application.
   *
   * @param args
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .sources(Bootstrap.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}

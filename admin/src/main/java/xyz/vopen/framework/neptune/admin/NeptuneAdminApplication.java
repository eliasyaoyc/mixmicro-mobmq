package xyz.vopen.framework.neptune.admin;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * {@link NeptuneAdminApplication}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/5
 */
@SpringBootApplication
public class NeptuneAdminApplication {

  /**
   * Startup Scheduler Admin API Application.
   *
   * @param args
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .sources(NeptuneAdminApplication.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}

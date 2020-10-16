package xyz.vopen.framework.neptune.client;

import org.springframework.context.ApplicationContext;

/**
 * {@link ApplicationContextUtil}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/16
 */
public class ApplicationContextUtil {
  private static boolean supportSpringBean = false;
  private static ApplicationContext applicationContext;

  public static void inject(final ApplicationContext ctx) {
    applicationContext = ctx;
    supportSpringBean = true;
  }

  public static boolean supportSpringBean() {
    return supportSpringBean;
  }

  public static <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  public static <T> T getBean(String className) throws Exception {
    ClassLoader classLoader = applicationContext.getClassLoader();
    if (classLoader != null) {
      return (T) applicationContext.getBean(classLoader.loadClass(className));
    }
    String[] split = className.split("\\.");
    String beanName = split[split.length - 1];
    char[] cs = beanName.toCharArray();
    cs[0] += 32;
    return (T) applicationContext.getBean(String.valueOf(cs));
  }
}

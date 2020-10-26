package xyz.vopen.framework.neptune.common.enums;

/**
 * {@link ExpressionType}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/26
 */
public interface ExpressionType {
  int CRON = 1;
  int API = 2;
  int FIX_RATE = 3;
  int FIX_DELAY = 4;
}

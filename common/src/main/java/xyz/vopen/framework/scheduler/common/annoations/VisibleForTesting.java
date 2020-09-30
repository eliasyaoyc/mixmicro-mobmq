package xyz.vopen.framework.scheduler.common.annoations;

import java.lang.annotation.*;

/**
 * {@link VisibleForTesting} This annotations declares that a function, field, constructor, or
 * entire type, is only visible for testing purposes.
 *
 * <p>This annotation is typically attached when for example a method should be {@code private}
 * (because it is not intended to be called externally), but cannot be declared private, because
 * some tests need to have access to it.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface VisibleForTesting {}

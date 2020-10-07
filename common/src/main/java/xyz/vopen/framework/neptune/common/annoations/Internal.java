package xyz.vopen.framework.neptune.common.annoations;

import java.lang.annotation.*;


/**
 * {@link Internal}
 * Annotation to mark methods within stable, public APIs as an internal developer API.
 *
 * <p>Developer APIs are stable but internal to Scheduler and might change across releases.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface Internal {
}

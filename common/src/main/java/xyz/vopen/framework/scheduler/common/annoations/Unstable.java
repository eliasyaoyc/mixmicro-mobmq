package xyz.vopen.framework.scheduler.common.annoations;

import java.lang.annotation.*;

/**
 * {@link Unstable} Signifies that a public API (public class, method or field) is subject to
 * incompatible changes, or even removal, in a future release. An API bearing this annotation is
 * exempt from any compatibility guarantees made by its containing library. Note that the presence
 * of this annotation implies nothing about the quality or performance of the API in question, only
 * the fact that it is not "API-frozen."
 *
 * <p>It is generally safe for <i>applications</i> to depend on beta APIs, at the cost of some extra
 * work during upgrades. However it is generally inadvisable for <i>libraries</i> (which get
 * included on users' CLASSPATHs, outside the library developers' control) to do so.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/9/30
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
  ElementType.ANNOTATION_TYPE,
  ElementType.FIELD,
  ElementType.CONSTRUCTOR,
  ElementType.TYPE,
  ElementType.METHOD
})
public @interface Unstable {}

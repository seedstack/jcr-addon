package org.seedstack.jcr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation accompanies the {@link org.seedstack.seed.transaction.Transactional} annotation
 * to define the transaction as a JCR one and specify the concerned JCR repository.
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JcrRepository {

    /**
     * @return the configured jcr repository name.
     */
    String value() default "";
}

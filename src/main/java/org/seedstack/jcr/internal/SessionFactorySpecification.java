/**
 * 
 */
package org.seedstack.jcr.internal;

import java.lang.reflect.Modifier;

import org.kametic.specifications.AbstractSpecification;
import org.seedstack.jcr.spi.JcrSessionFactory;
import org.seedstack.shed.reflect.ClassPredicates;

public class SessionFactorySpecification extends AbstractSpecification<Class<?>> {
    public static final SessionFactorySpecification INSTANCE = new SessionFactorySpecification();

    private SessionFactorySpecification() {
        // cannot be instancied
    }

    @Override
    public boolean isSatisfiedBy(Class<?> candidate) {
        return ClassPredicates
                .classModifierIs(Modifier.ABSTRACT).negate()
                .and(ClassPredicates.classImplements(JcrSessionFactory.class))
                .test(candidate);

    }
}

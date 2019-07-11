package org.seedstack.jcr.internal;

import java.lang.reflect.Modifier;

import org.kametic.specifications.AbstractSpecification;
import org.seedstack.jcr.spi.JcrTranslator;
import org.seedstack.shed.reflect.ClassPredicates;

class JcrTranslatorSpecification extends AbstractSpecification<Class<?>> {

    public static final JcrTranslatorSpecification INSTANCE = new JcrTranslatorSpecification();

    private JcrTranslatorSpecification() {
        // cannot be instancied
    }

    @Override
    public boolean isSatisfiedBy(Class<?> candidate) {
        return ClassPredicates
                .classModifierIs(Modifier.ABSTRACT).negate()
                .and(ClassPredicates.classImplements(JcrTranslator.class))
                .test(candidate);

    }

}

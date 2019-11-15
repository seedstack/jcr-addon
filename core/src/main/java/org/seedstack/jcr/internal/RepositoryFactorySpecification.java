/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.jcr.internal;

import java.lang.reflect.Modifier;

import org.kametic.specifications.AbstractSpecification;
import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.shed.reflect.ClassPredicates;

public class RepositoryFactorySpecification extends AbstractSpecification<Class<?>> {
    public static final RepositoryFactorySpecification INSTANCE = new RepositoryFactorySpecification();

    private RepositoryFactorySpecification() {
        // cannot be instancied
    }

    @Override
    public boolean isSatisfiedBy(Class<?> candidate) {
        return ClassPredicates
                .classModifierIs(Modifier.ABSTRACT).negate()
                .and(ClassPredicates.classImplements(JcrRepositoryFactory.class))
                .test(candidate);

    }
}

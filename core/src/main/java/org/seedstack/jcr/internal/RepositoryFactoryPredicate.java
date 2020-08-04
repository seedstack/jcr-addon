/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr.internal;

import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.shed.reflect.ClassPredicates;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class RepositoryFactoryPredicate implements Predicate<Class<?>> {
    public static final RepositoryFactoryPredicate INSTANCE = new RepositoryFactoryPredicate();

    private RepositoryFactoryPredicate() {
        // cannot be instantiated
    }

    @Override
    public boolean test(Class<?> candidate) {
        return ClassPredicates
                .classModifierIs(Modifier.ABSTRACT).negate()
                .and(ClassPredicates.classImplements(JcrRepositoryFactory.class))
                .test(candidate);
    }
}

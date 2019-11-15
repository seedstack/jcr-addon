/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.lang.reflect.Method;

import org.seedstack.shed.reflect.StandardAnnotationResolver;

public class JcrRepositoryResolver
        extends StandardAnnotationResolver<Method, org.seedstack.jcr.JcrRepository> {

    public static final JcrRepositoryResolver INSTANCE = new JcrRepositoryResolver();

    private JcrRepositoryResolver() {
        // No Instances!
    }
}

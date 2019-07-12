/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.seedstack.jcr.JcrConfig;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.core.internal.jndi.JndiPlugin;
import org.seedstack.seed.core.internal.transaction.TransactionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;

/** This plugin provides JCR support through JNDI or plain configuration. */
public class JcrPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcrPlugin.class);
    private JcrConfig jcrConfig;
    private final List<Class<?>> factories = new ArrayList<>();

    @Override
    public Collection<Class<?>> dependencies() {
        return Lists.newArrayList(JndiPlugin.class, TransactionPlugin.class);
    }

    @Override
    public String name() {
        return "jcr";
    }

    @Override
    public InitState initialize(InitContext initContext) {
        jcrConfig = getConfiguration(JcrConfig.class);

        factories.addAll(initContext.scannedTypesBySpecification()
                .get(SessionFactorySpecification.INSTANCE));

        LOGGER.info(
                "Initializing JCR Plugin with {} factories and {} configurations",
                factories.size(), jcrConfig.getSessions().size());

        return InitState.INITIALIZED;
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .specification(SessionFactorySpecification.INSTANCE)
                .build();
    }

    @Override
    public Object nativeUnitModule() {

        return new JcrModule(factories, jcrConfig);
    }

}

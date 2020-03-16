/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.seed.SeedException;
import org.seedstack.shed.misc.PriorityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

@JcrConcern
class JcrModule extends PrivateModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcrModule.class);

    private final JcrConfig configuration;
    private final List<Class<? extends JcrRepositoryFactory>> factories;
    private final List<JcrRepositoryFactory> factoryInstances = new ArrayList<>();

    JcrModule(List<Class<? extends JcrRepositoryFactory>> factories, JcrConfig jcrConfig) {
        this.factories = factories;
        configuration = jcrConfig;
    }

    @Override
    protected void configure() {
        PriorityUtils.sortByPriority(factories);
        this.initializeFactoryInstances(factories);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("JCR Plug-in configuration {}", configuration);
        }

        configuration.getRepositories().forEach(
                (key, value) -> bindConfiguration(key, value, key.equals(configuration.getDefaultRepository())));

    }

    private void bindConfiguration(String name, RepositoryConfig configuration, boolean isDefault) {

        LOGGER.debug("Binding repository: {}", name);
        LOGGER.trace("Configuration {}", configuration);

        JcrSessionProvider sessionProvider = new JcrSessionProvider(configuration, this.factoryInstances);

        bind(Session.class).annotatedWith(Names.named(name)).toProvider(sessionProvider);
        expose(Session.class).annotatedWith(Names.named(name));

        if (isDefault) {
            LOGGER.trace("Binding default repository with configuration: {}", configuration);
            bind(Session.class).toProvider(sessionProvider);
            expose(Session.class);
        }

    }

    private void initializeFactoryInstances(List<Class<? extends JcrRepositoryFactory>> factories) {
        for (Class<? extends JcrRepositoryFactory> factoryClass : factories) {
            try {
                factoryInstances.add(factoryClass.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                throw SeedException.wrap(ex, JcrErrorCode.CANNOT_CREATE_FACTORY).put("factoryClass", factoryClass);
            }
        }
    }

}

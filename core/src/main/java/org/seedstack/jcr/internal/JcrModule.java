/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.List;

import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrExceptionHandler;
import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.seed.core.internal.transaction.TransactionalProxy;
import org.seedstack.shed.misc.PriorityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

class JcrModule extends PrivateModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcrModule.class);

    private final JcrConfig configuration;
    private final List<Class<? extends JcrRepositoryFactory>> factories;

    JcrModule(List<Class<? extends JcrRepositoryFactory>> factories,
            JcrConfig jcrConfig) {
        this.factories = factories;
        configuration = jcrConfig;
    }

    @Override
    protected void configure() {
        PriorityUtils.sortByPriority(factories);
        JcrTransactionLink transactionalLink = new JcrTransactionLink();
        bind(Session.class).toInstance(TransactionalProxy.create(Session.class, transactionalLink));
        expose(Session.class);
        configuration.getRepositories()
                .forEach((key, value) -> bindConfiguration(key, value, transactionalLink));

    }

    private void bindConfiguration(String name, RepositoryConfig configuration,
            JcrTransactionLink transactionalLink) {

        LOGGER.debug("Binding repository: {}", name);
        LOGGER.trace("Configuration {}", configuration);

        Class<? extends JcrExceptionHandler> configExceptionHandler = configuration
                .getExceptionHandler();

        if (configExceptionHandler != null) {
            bind(JcrExceptionHandler.class).annotatedWith(Names.named(name))
                    .to(configExceptionHandler);
        } else {
            bind(JcrExceptionHandler.class).annotatedWith(Names.named(name))
                    .toProvider(Providers.of(null));
        }
        expose(JcrExceptionHandler.class).annotatedWith(Names.named(name));

        JcrTransactionHandler transactionHandler = new JcrTransactionHandler(transactionalLink,
                factories, configuration);
        bind(JcrTransactionHandler.class).annotatedWith(Names.named(name))
                .toInstance(transactionHandler);
        expose(JcrTransactionHandler.class).annotatedWith(Names.named(name));

    }

}

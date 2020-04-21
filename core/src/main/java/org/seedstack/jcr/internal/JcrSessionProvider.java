/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Provider;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.seed.SeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JcrSessionProvider implements Provider<Session> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcrSessionProvider.class);
    private final String configName;

    private final RepositoryConfig configuration;
    private final List<JcrRepositoryFactory> factoryInstances;

    JcrSessionProvider(String configName, RepositoryConfig configuration,
            List<JcrRepositoryFactory> factoryInstances) {

        this.configName = configName;
        this.configuration = configuration;
        if (configuration.getRepositoryFactory() != null) {
            this.factoryInstances = factoryInstances.stream()
                    .filter(x -> configuration.getRepositoryFactory()
                            .isAssignableFrom(x.getClass()))
                    .collect(Collectors.toList());
        } else {
            this.factoryInstances = Collections.unmodifiableList(factoryInstances);
        }
        LOGGER.trace("Factories: {}", this.factoryInstances);

    }

    @Override
    public Session get() {

        LOGGER.trace("Providing instance for {}", configuration.getAddress());
        for (JcrRepositoryFactory factory : factoryInstances) {
            try {
                Session session = factory.createSession(configuration);
                if (session != null) {
                    return session;
                }
            } catch (RepositoryException e) {
                LOGGER.debug("Could not acquire a session for {} with {} due {}", configuration,
                        factory, e);
            }
        }
        throw SeedException.createNew(JcrErrorCode.CANNOT_CREATE_SESSION).put("address",
                configuration.getAddress());
    }

    public String getConfigurationName() {
        return configName;
    }

}

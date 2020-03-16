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

    private final RepositoryConfig configuration;
    private final List<JcrRepositoryFactory> factoryInstances;

    private static final Logger LOGGER = LoggerFactory.getLogger(JcrSessionProvider.class);

    JcrSessionProvider(RepositoryConfig configuration, List<JcrRepositoryFactory> factoryInstances) {
        this.configuration = configuration;
        if (configuration.getRepositoryFactory() != null) {
            this.factoryInstances = factoryInstances.stream()
                    .filter(x -> configuration.getRepositoryFactory().isAssignableFrom(x.getClass()))
                    .collect(Collectors.toList());
        } else {
            this.factoryInstances = Collections.unmodifiableList(factoryInstances);
        }
        LOGGER.trace("Factories: {}", this.factoryInstances);

    }

    @Override
    public Session get() {

        LOGGER.trace("Getting instance for {}", configuration.getAddress());
        for (JcrRepositoryFactory factory : factoryInstances) {
            try {
                Session session = factory.createSession(configuration);
                if (session != null) {
                    return session;
                }
            } catch (RepositoryException e) {
                LOGGER.debug("Could not acquire a session for {} with {} due {}", configuration, factory, e);
            }
        }
        throw SeedException.createNew(JcrErrorCode.CANNOT_CREATE_SESSION).put("address", configuration.getAddress());
    }

}

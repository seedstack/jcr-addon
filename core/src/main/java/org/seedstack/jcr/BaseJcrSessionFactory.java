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
package org.seedstack.jcr;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrRepositoryFactory;

public abstract class BaseJcrSessionFactory implements JcrRepositoryFactory {

    private static final ServiceLoader<RepositoryFactory> factories = ServiceLoader
            .load(RepositoryFactory.class);

    @Override
    public Session createSession(RepositoryConfig configuration) throws RepositoryException {

        Map<String, String> repositoryParameters = translateConfiguration(configuration);
        repositoryParameters = appendVendorProperties(configuration, repositoryParameters);

        // Loop over every factory implementation and look for one that accepts our parameters
        for (RepositoryFactory factory : factories) {
            Repository repo = factory.getRepository(repositoryParameters);
            if (repo != null) {
                if (configuration.hasAuthenticationInfo()) {
                    return repo.login(new SimpleCredentials(configuration.getUsername(),
                            configuration.getPassword().toCharArray()));
                }
                return repo.login();
            }
        }
        return null;
    }

    protected Map<String, String> appendVendorProperties(RepositoryConfig config,
            Map<String, String> parsedConfig) {

        Map<String, String> transaltedConfig = new HashMap<>();
        transaltedConfig.putAll(parsedConfig);

        if (config.getVendorProperties() != null) {
            for (Entry<Object, Object> property : config.getVendorProperties().entrySet()) {
                transaltedConfig.put(property.getKey().toString(), property.getValue().toString());
            }
        }

        return transaltedConfig;

    }

}

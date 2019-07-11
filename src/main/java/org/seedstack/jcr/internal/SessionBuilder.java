/**
 * 
 */
package org.seedstack.jcr.internal;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrTranslator;

class SessionBuilder {

    private static final ServiceLoader<RepositoryFactory> factories = ServiceLoader
            .load(RepositoryFactory.class);

    private SessionBuilder() {
        // Cannot be instanced
    }

    public static Session buildSession(RepositoryConfig config,
            Collection<JcrTranslator> configurationTranslators) throws RepositoryException {

        for (JcrTranslator translator : configurationTranslators) {
            Map<String, String> configuration = translator.translate(config);

            // Loop over every factory impl and look for one that accepts our parameters
            for (RepositoryFactory factory : factories) {
                Repository repo = factory.getRepository(configuration);
                if (repo != null) {
                    if (config.hasAuthenticationInfo()) {
                        return repo.login(new SimpleCredentials(config.getUsername(),
                                config.getPassword().toCharArray()));
                    }
                    return repo.login();
                }
            }

        }
        return null;
    }

}

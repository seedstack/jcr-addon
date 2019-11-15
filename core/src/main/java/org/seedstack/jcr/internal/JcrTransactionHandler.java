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
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrRepositoryFactory;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrTransactionHandler implements TransactionHandler<Session> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcrTransactionHandler.class);
    private final RepositoryConfig configuration;
    private final List<JcrRepositoryFactory> factoryInstances = new ArrayList<>();
    private final JcrTransactionLink transactionalLink;

    /**
     * @param transactionalLink
     * @param factories
     * @param configuration
     */
    public JcrTransactionHandler(JcrTransactionLink transactionalLink,
            List<Class<? extends JcrRepositoryFactory>> factories, RepositoryConfig configuration) {
        this.transactionalLink = transactionalLink;
        this.configuration = configuration;

        if (configuration.getRepositoryFactory() != null) {
            initializeFactoryInstances(
                    factories.stream().filter(x -> x.equals(configuration.getRepositoryFactory()))
                            .collect(Collectors.toList()));
        } else {
            initializeFactoryInstances(factories);
        }
    }

    @Override
    public void doBeginTransaction(Session currentTransaction) {
        try {
            currentTransaction.refresh(false);
        } catch (RepositoryException e) {
            throw SeedException.wrap(e, JcrErrorCode.SESSION_CANNOT_BE_RESTORED);
        }
    }

    @Override
    public void doCleanup() {
        transactionalLink.pop().logout();
    }

    @Override
    public void doCommitTransaction(Session currentTransaction) {
        try {
            currentTransaction.save();
        } catch (RepositoryException e) {
            throw SeedException.wrap(e, JcrErrorCode.SESSION_SAVE_COULD_NOT_BE_EXECUTED);
        }
    }

    @Override
    public Session doCreateTransaction() {
        return transactionalLink.get();
    }

    @Override
    public void doInitialize(TransactionMetadata transactionMetadata) {
        for (JcrRepositoryFactory factory : factoryInstances) {
            try {
                Session session = factory.createSession(configuration);
                if (session != null) {
                    transactionalLink.push(session);
                    return;
                }
            } catch (RepositoryException e) {
                LOGGER.debug("Could not acquire a session for {} with {} due {}", configuration,
                        factory, e);
            }
        }
        throw SeedException.createNew(JcrErrorCode.CANNOT_CREATE_SESSION).put("config",
                transactionMetadata.getResource());
    }

    @Override
    public void doJoinGlobalTransaction() {
        // DO NOTHING
    }

    @Override
    public void doMarkTransactionAsRollbackOnly(Session currentTransaction) {
        // DO NOTHING
    }

    @Override
    public void doReleaseTransaction(Session currentTransaction) {
        // DO NOTHING
    }

    @Override
    public void doRollbackTransaction(Session currentTransaction) {
        try {
            currentTransaction.refresh(false);
        } catch (RepositoryException e) {
            throw SeedException.wrap(e, JcrErrorCode.SESSION_CANNOT_BE_RESTORED);
        }
    }

    @Override
    public Session getCurrentTransaction() {
        return null;
    }

    private void initializeFactoryInstances(List<Class<? extends JcrRepositoryFactory>> factories) {
        for (Class<? extends JcrRepositoryFactory> factoryClass : factories) {
            try {
                factoryInstances.add(factoryClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw SeedException.wrap(e, JcrErrorCode.CANNOT_CREATE_FACTORY)
                        .put("factoryClass", factoryClass);
            }
        }
    }

}

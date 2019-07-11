/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import org.seedstack.jcr.spi.ConnectionDefinition;
import org.seedstack.jcr.spi.JcrExceptionHandler;
import org.seedstack.jcr.spi.JmsFactory;
import org.seedstack.jcr.spi.MessageListenerDefinition;
import org.seedstack.jcr.spi.MessageListenerInstanceDefinition;
import org.seedstack.jcr.spi.MessagePoller;
import org.seedstack.seed.core.internal.transaction.TransactionalProxy;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class JcrModule extends AbstractModule {
    private final JmsFactory jmsFactory;
    private final Map<String, Connection> connections;
    private final Map<String, MessageListenerDefinition> messageListenerDefinitions;
    private final Map<String, ConnectionDefinition> connectionDefinitions;
    private final Collection<MessagePoller> pollers;

    public JcrModule() {
        //TODO: Stuff
    }

    @Override
    protected void configure() {
        requestStaticInjection(ExceptionListenerAdapter.class);
        requestStaticInjection(MessageListenerAdapter.class);

        bind(JmsFactory.class).toInstance(jmsFactory);
        requestInjection(jmsFactory);

        JmsSessionLink jmsSessionLink = new JmsSessionLink();
        bind(Session.class).toInstance(TransactionalProxy.create(Session.class, jmsSessionLink));

        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            bindConnection(connectionDefinitions.get(entry.getKey()), entry.getValue(), jmsSessionLink);
        }

        for (Map.Entry<String, MessageListenerDefinition> entry : messageListenerDefinitions.entrySet()) {
            bindMessageListener(entry.getValue());
        }

        for (MessagePoller poller : pollers) {
            requestInjection(poller);
        }
    }

    private void bindMessageListener(MessageListenerDefinition messageListenerDefinition) {
        String name = messageListenerDefinition.getName();

        bind(JmsListenerTransactionHandler.class)
                .annotatedWith(Names.named(name))
                .toInstance(new JmsListenerTransactionHandler(messageListenerDefinition.getSession()));

        if (messageListenerDefinition instanceof MessageListenerInstanceDefinition) {
            MessageListener messageListener = ((MessageListenerInstanceDefinition) messageListenerDefinition).getMessageListener();
            bind(MessageListener.class).annotatedWith(Names.named(name)).toInstance(messageListener);
        } else {
            bind(MessageListener.class).annotatedWith(Names.named(name)).to(messageListenerDefinition.getMessageListenerClass());
        }
    }


    private void bindConnection(ConnectionDefinition connectionDefinition, Connection connection, JmsSessionLink jmsSessionLink) {
        String name = connectionDefinition.getName();

        Class<? extends JcrExceptionHandler> jmsExceptionHandlerClass = connectionDefinition.getJmsExceptionHandlerClass();
        if (jmsExceptionHandlerClass != null) {
            bind(JcrExceptionHandler.class).annotatedWith(Names.named(name)).to(jmsExceptionHandlerClass);
        } else {
            bind(JcrExceptionHandler.class).annotatedWith(Names.named(name)).toProvider(Providers.of(null));
        }

        if (connectionDefinition.getExceptionListenerClass() != null) {
            bind(ExceptionListener.class).annotatedWith(Names.named(name)).to(connectionDefinition.getExceptionListenerClass());
        }

        bind(Connection.class).annotatedWith(Names.named(name)).toInstance(connection);

        JmsTransactionHandler transactionHandler = new JmsTransactionHandler(jmsSessionLink, connection);
        bind(JmsTransactionHandler.class).annotatedWith(Names.named(name)).toInstance(transactionHandler);
    }
}

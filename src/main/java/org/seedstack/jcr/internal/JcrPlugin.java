/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.JobAttributes.DestinationType;
import java.beans.ExceptionListener;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.naming.Context;
import javax.print.attribute.standard.Destination;

import org.apache.commons.lang.StringUtils;
import org.kametic.specifications.Specification;
import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.JmsMessageListener;
import org.seedstack.jcr.spi.JcrTranslator;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.core.internal.jndi.JndiPlugin;
import org.seedstack.seed.core.internal.transaction.TransactionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.spi.Message;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;

/** This plugin provides JCR support through JNDI or plain configuration. */
public class JcrPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcrPlugin.class);

    private final Specification<Class<?>> translators = classImplements(
            JcrTranslator.class);

    private TransactionPlugin transactionPlugin;

    private final Map<String, Session> jcrSessions = new HashMap<>();

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
        transactionPlugin = initContext.dependency(TransactionPlugin.class);
        JcrConfig jcrConfig = getConfiguration(JcrConfig.class);
        Map<String, Context> jndiContexts = initContext.dependency(JndiPlugin.class)
                .getJndiContexts();
        
        

        configureSessions(jcrConfig);

        return InitState.INITIALIZED;
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        // TODO: Specify
        return classpathScanRequestBuilder()
                .specification(translators)
                .build();
    }

    @Override
    public Object nativeUnitModule() {
        return new JcrModule();
    }

    private void configureSessions(JcrConfig config, Collection<JcrTranslator> translators) {
        try {

            for (Entry<String, RepositoryConfig> repository : config.getRepositories().entrySet()) {
                Session session = SessionBuilder.buildSession(repository.getValue(), translators);
                if (session == null) {
                    // TODO: BOOM
                }
                this.jcrSessions.put(repository.getKey(), session);
            }
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void configureMessageListeners(Collection<Class<?>> listenerCandidates) {
        for (Class<?> candidate : listenerCandidates) {
            if (MessageListener.class.isAssignableFrom(candidate)) {
                // noinspection unchecked
                Class<? extends MessageListener> messageListenerClass = (Class<? extends MessageListener>) candidate;
                String messageListenerName = messageListenerClass.getCanonicalName();
                JmsMessageListener annotation = messageListenerClass
                        .getAnnotation(JmsMessageListener.class);

                boolean isTransactional;
                try {
                    isTransactional = transactionPlugin.isTransactional(
                            messageListenerClass.getMethod("onMessage", Message.class));
                } catch (NoSuchMethodException e) {
                    throw SeedException.wrap(e, JmsErrorCode.INVALID_MESSAGE_LISTENER_CLASS)
                            .put("messageListenerClass", messageListenerClass.getName());
                }

                Connection listenerConnection = connections.get(annotation.connection());

                if (listenerConnection == null) {
                    throw SeedException.createNew(JmsErrorCode.MISSING_CONNECTION_FACTORY)
                            .put(ERROR_CONNECTION_NAME, annotation.connection())
                            .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerName);
                }

                Session session;
                try {
                    session = listenerConnection.createSession(isTransactional,
                            Session.AUTO_ACKNOWLEDGE);
                } catch (JMSException e) {
                    throw SeedException.wrap(e, JmsErrorCode.UNABLE_TO_CREATE_SESSION)
                            .put(ERROR_CONNECTION_NAME, annotation.connection())
                            .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerName);
                }

                Destination destination;
                DestinationType destinationType;

                if (!annotation.destinationTypeStr().isEmpty()) {
                    try {
                        destinationType = DestinationType.valueOf(
                                application.substituteWithConfiguration(
                                        annotation.destinationTypeStr()));
                    } catch (IllegalArgumentException e) {
                        throw SeedException.wrap(e, JmsErrorCode.UNKNOWN_DESTINATION_TYPE)
                                .put(ERROR_DESTINATION_TYPE, annotation.destinationTypeStr())
                                .put(ERROR_CONNECTION_NAME, annotation.connection())
                                .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerName);
                    }
                } else {
                    destinationType = annotation.destinationType();
                }
                try {
                    switch (destinationType) {
                    case QUEUE:
                        destination = session.createQueue(
                                application
                                        .substituteWithConfiguration(annotation.destinationName()));
                        break;
                    case TOPIC:
                        destination = session.createTopic(
                                application
                                        .substituteWithConfiguration(annotation.destinationName()));
                        break;
                    default:
                        throw SeedException.createNew(JmsErrorCode.UNKNOWN_DESTINATION_TYPE)
                                .put(ERROR_DESTINATION_TYPE, destinationType)
                                .put(ERROR_CONNECTION_NAME, annotation.connection())
                                .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerName);
                    }
                } catch (JMSException e) {
                    throw SeedException.wrap(e, JmsErrorCode.UNABLE_TO_CREATE_DESTINATION)
                            .put(ERROR_DESTINATION_TYPE, destinationType.name())
                            .put(ERROR_CONNECTION_NAME, annotation.connection())
                            .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerName);
                }

                Class<? extends MessagePoller> messagePollerClass = null;
                if (annotation.poller().length > 0) {
                    messagePollerClass = annotation.poller()[0];
                }

                registerMessageListener(
                        new MessageListenerDefinition(
                                messageListenerName,
                                application.substituteWithConfiguration(annotation.connection()),
                                session,
                                destination,
                                application.substituteWithConfiguration(annotation.selector()),
                                messageListenerClass,
                                messagePollerClass));
            }
        }
    }

    private MessageConsumer createMessageConsumer(
            MessageListenerDefinition messageListenerDefinition)
            throws JMSException {
        LOGGER.debug("Creating JMS consumer for listener {}", messageListenerDefinition.getName());

        MessageConsumer consumer;
        Session session = messageListenerDefinition.getSession();

        if (StringUtils.isNotBlank(messageListenerDefinition.getSelector())) {
            consumer = session.createConsumer(
                    messageListenerDefinition.getDestination(),
                    messageListenerDefinition.getSelector());
        } else {
            consumer = session.createConsumer(messageListenerDefinition.getDestination());
        }

        MessagePoller messagePoller;
        if (messageListenerDefinition.getPoller() != null) {
            try {
                LOGGER.debug("Creating poller for JMS listener {}",
                        messageListenerDefinition.getName());

                Connection connection = connections
                        .get(messageListenerDefinition.getConnectionName());

                messagePoller = messageListenerDefinition.getPoller().newInstance();
                messagePoller.setSession(session);
                messagePoller.setMessageConsumer(consumer);
                messagePoller.setMessageListener(
                        new MessageListenerAdapter(messageListenerDefinition.getName()));

                if (connection instanceof ManagedConnection) {
                    messagePoller.setExceptionListener((ExceptionListener) connection);
                } else {
                    messagePoller.setExceptionListener(connection.getExceptionListener());
                }
            } catch (Exception e) {
                throw SeedException.wrap(e, JmsErrorCode.UNABLE_TO_CREATE_POLLER)
                        .put("pollerClass", messageListenerDefinition.getPoller());
            }

            pollers.put(messageListenerDefinition.getName(), messagePoller);
        } else {
            consumer.setMessageListener(
                    new MessageListenerAdapter(messageListenerDefinition.getName()));
        }

        return consumer;
    }

    /**
     * Register an existing JMS connection to be managed by the JMS plugin.
     *
     * @param connection
     *                                 the connection.
     * @param connectionDefinition
     *                                 the connection definition.
     */
    public void registerConnection(Connection connection,
            ConnectionDefinition connectionDefinition) {
        checkNotNull(connection);
        checkNotNull(connectionDefinition);

        if (this.connectionDefinitions.putIfAbsent(connectionDefinition.getName(),
                connectionDefinition) != null) {
            throw SeedException.createNew(JmsErrorCode.DUPLICATE_CONNECTION_NAME)
                    .put(ERROR_CONNECTION_NAME, connectionDefinition.getName());
        }

        if (this.connections.putIfAbsent(connectionDefinition.getName(), connection) != null) {
            throw SeedException.createNew(JmsErrorCode.DUPLICATE_CONNECTION_NAME)
                    .put(ERROR_CONNECTION_NAME, connectionDefinition.getName());
        }

        if (shouldStartConnections.get()) {
            try {
                connection.start();
            } catch (JMSException e) {
                throw SeedException.wrap(e, JmsErrorCode.UNABLE_TO_START_JMS_CONNECTION)
                        .put(ERROR_CONNECTION_NAME, connectionDefinition.getName());
            }
        }
    }

    /**
     * Register a message listener definition to be managed by the JMS plugin.
     *
     * @param messageListenerDefinition
     *                                      the message listener definition.
     */
    public void registerMessageListener(MessageListenerDefinition messageListenerDefinition) {
        checkNotNull(messageListenerDefinition);

        ConnectionDefinition connectionDefinition = connectionDefinitions
                .get(messageListenerDefinition.getConnectionName());
        if (connectionDefinition.isJeeMode() && messageListenerDefinition.getPoller() == null) {
            throw SeedException.createNew(JmsErrorCode.MESSAGE_POLLER_REQUIRED_IN_JEE_MODE)
                    .put(ERROR_CONNECTION_NAME, connectionDefinition.getName())
                    .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerDefinition.getName());
        }

        try {
            createMessageConsumer(messageListenerDefinition);
        } catch (JMSException e) {
            throw SeedException.wrap(e, JmsErrorCode.UNABLE_TO_CREATE_MESSAGE_CONSUMER)
                    .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerDefinition.getName());
        }

        if (messageListenerDefinitions.putIfAbsent(
                messageListenerDefinition.getName(), messageListenerDefinition) != null) {
            throw SeedException.createNew(JmsErrorCode.DUPLICATE_MESSAGE_LISTENER_NAME)
                    .put(ERROR_MESSAGE_LISTENER_NAME, messageListenerDefinition.getName());
        }
    }

    /**
     * Retrieve a connection by name.
     *
     * @param name
     *                 the name of the connection to retrieve.
     * @return the connection or null if it doesn't exists.
     */
    public Connection getConnection(String name) {
        return connections.get(name);
    }

    /**
     * Return the factory used to create JMS objects.
     *
     * @return the JMS factory.
     */
    public JmsFactory getJmsFactory() {
        return jmsFactory;
    }
}

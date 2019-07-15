/**
 * 
 */
package org.seedstack.jcr.fixtures;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Priority;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig.SessionConfig;
import org.seedstack.jcr.spi.JcrSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(1000)
public class BogusFactory implements JcrSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BogusFactory.class);

    private static CreationBehaviour behaviour = CreationBehaviour.NULL;

    public enum CreationBehaviour {
        CRASH, NULL
    }

    public static void setCreationBehaviour(CreationBehaviour behaviour) {
        BogusFactory.behaviour = behaviour;
    }

    @Override
    public Session createSession(SessionConfig configuration) throws RepositoryException {

        LOGGER.info("Call to bogus factory with behaviour: {}", behaviour);
        if (behaviour == CreationBehaviour.CRASH) {
            throw new BoggusException();
        }
        return null;

    }

    @Override
    public Map<String, String> translateConfiguration(SessionConfig config) {
        return Collections.emptyMap();
    }

}

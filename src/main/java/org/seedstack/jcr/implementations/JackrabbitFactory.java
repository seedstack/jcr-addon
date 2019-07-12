/**
 * 
 */
package org.seedstack.jcr.implementations;

import java.util.HashMap;
import java.util.Map;

import org.seedstack.jcr.BaseJcrSessionFactory;
import org.seedstack.jcr.JcrConfig.SessionConfig;

public class JackrabbitFactory extends BaseJcrSessionFactory {
    private static final String REPOSITORY_HOME = "org.apache.jackrabbit.repository.home";
    private static final String REPOSITORY_URI = "org.apache.jackrabbit.repository.uri";
    private static final String JNDI_NAME = "org.apache.jackrabbit.repository.jndi.name";

    @Override
    public Map<String, String> translateConfiguration(SessionConfig config) {
        Map<String, String> translated = new HashMap<>();

        switch (config.getType()) {
        case JNDI_NAME:
            translated.put(JNDI_NAME, config.getAddress());
            break;
        case JNDI_URI:
        case REMOTE_URI:
            translated.put(REPOSITORY_URI, config.getAddress());
            break;
        case LOCAL_PATH:
            translated.put(REPOSITORY_HOME, config.getAddress());
            break;
        default:
            break;
        }

        return translated;
    }

}

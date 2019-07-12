package org.seedstack.jcr.spi;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.SessionConfig;

public interface JcrSessionFactory {

    Session createSession(JcrConfig.SessionConfig configuration) throws RepositoryException;

    Map<String, String> translateConfiguration(SessionConfig config);

}

package org.seedstack.jcr.fixtures;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.WithContentRepository;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

class TestConstructorFixtureImpl implements TestConstructorFixture {

    private final Session defaultSession;

    @Inject
    public TestConstructorFixtureImpl(Session defaultSession, @Named("anonymous") Session anonSession) {
        this.defaultSession = defaultSession;
    }

    @Logging
    private Logger logger;

    @Override
    @WithContentRepository
    public void testSession() throws RepositoryException {
        logger.warn("Invocation in progress");
        defaultSession.getRootNode();
    }

}

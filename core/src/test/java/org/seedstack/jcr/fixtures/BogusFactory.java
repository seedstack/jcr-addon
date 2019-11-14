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
package org.seedstack.jcr.fixtures;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Priority;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrRepositoryFactory;

@Priority(1000)
public class BogusFactory implements JcrRepositoryFactory {

    private static int callCount = 0;

    public static void resetCallCount() {
        callCount = 0;
    }

    public static int getCallCount() {
        return callCount;
    }

    @Override
    public synchronized Session createSession(RepositoryConfig configuration)
            throws RepositoryException {
        callCount += 1;
        throw new BoggusException();
    }

    @Override
    public Map<String, String> translateConfiguration(RepositoryConfig config) {
        return Collections.emptyMap();
    }

}

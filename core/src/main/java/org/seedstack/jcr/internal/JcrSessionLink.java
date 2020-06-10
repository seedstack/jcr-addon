/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr.internal;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.seedstack.seed.SeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JcrSessionLink {
    private final Logger LOGGER = LoggerFactory.getLogger(JcrSessionLink.class);
    private final ThreadLocal<Map<String, Session>> perThreadSessionMap = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<String, Integer>> perThreadHitMap = ThreadLocal.withInitial(HashMap::new);

    private final Map<String, JcrSessionProvider> providers = new HashMap<>();

    public Session getThreadSession(String sessionKey) {
        return perThreadSessionMap.get().get(sessionKey);
    }

    void createSession(String sessionKey) {
        Map<String, Session> sessionMap = perThreadSessionMap.get();
        Map<String, Integer> hitMap = perThreadHitMap.get();

        try {
            if (sessionMap.containsKey(sessionKey)) {
                return;
            }
            if (!providers.containsKey(sessionKey)) {
                throw SeedException.createNew(JcrErrorCode.CANNOT_LOCATE_PROVIDER).put("sessionKey", sessionKey);
            }
            sessionMap.put(sessionKey, providers.get(sessionKey).get());
        } finally {
            hitMap.put(sessionKey, hitMap.getOrDefault(sessionKey, 0) + 1);
        }
    }

    void finishSession(String sessionKey) {
        Map<String, Session> sessionMap = perThreadSessionMap.get();
        Map<String, Integer> hitMap = perThreadHitMap.get();

        if (!hitMap.containsKey(sessionKey)) {
            LOGGER.warn("finishing an already closed session with key '{}'", sessionKey);
            return;
        }
        Integer hits = hitMap.get(sessionKey);
        try {
            if (hits > 1) {
                // There's still an open session with this key
                return;
            }
            Session session = sessionMap.get(sessionKey);
            if (!session.isLive()) {

                throw SeedException.createNew(JcrErrorCode.SESSION_ALREADY_DISPOSED);
            }
            session.save();
            session.logout();
        } catch (RepositoryException e) {
            sessionMap.remove(sessionKey);
            throw SeedException.wrap(e, JcrErrorCode.SESSION_DISPOSAL_FAILED);
        } finally {
            if (hits > 1) {
                hitMap.put(sessionKey, hits - 1);
            } else {
                sessionMap.remove(sessionKey);
                hitMap.remove(sessionKey);
            }
        }
    }

    void registerProvider(JcrSessionProvider provider) {
        this.providers.put(provider.getConfigurationName(), provider);
    }
}

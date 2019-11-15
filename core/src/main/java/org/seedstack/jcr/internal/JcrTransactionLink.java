/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.jcr.Session;

import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;

class JcrTransactionLink implements TransactionalLink<Session> {

    private final ThreadLocal<Deque<Session>> perThreadObjectContainer = ThreadLocal
            .withInitial(ArrayDeque::new);

    @Override
    public Session get() {
        Session session = this.perThreadObjectContainer.get().peek();

        if (session == null) {
            throw SeedException
                    .createNew(JcrErrorCode.ACCESSING_JCR_SESSION_OUTSIDE_TRANSACTION);
        }

        return session;
    }

    void push(Session session) {
        perThreadObjectContainer.get().push(session);
    }

    Session pop() {
        Deque<Session> sessions = perThreadObjectContainer.get();
        Session session = sessions.pop();
        if (sessions.isEmpty()) {
            perThreadObjectContainer.remove();
        }
        return session;
    }

}

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
package org.seedstack.jcr.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.jcr.Session;

class JcrSessionProxy implements InvocationHandler {

    private final JcrSessionLink sessionLink;
    private final String configName;

    private JcrSessionProxy(String configName, JcrSessionLink sessionLink) {
        this.sessionLink = sessionLink;
        this.configName = configName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Session instance = sessionLink.getThreadSession(configName);
        if (instance == null) {
            throw new RuntimeException("Session is not provided");
        }

        return method.invoke(instance, args);
    }

    static Session getProxyInstance(String configName, JcrSessionLink sessionLink) {

        return (Session) Proxy.newProxyInstance(JcrSessionProxy.class.getClassLoader(),
                new Class<?>[] { Session.class },
                new JcrSessionProxy(configName, sessionLink));
    }
}

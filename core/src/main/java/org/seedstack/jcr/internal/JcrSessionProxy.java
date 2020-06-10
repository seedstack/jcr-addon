/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.jcr.Session;
import org.seedstack.seed.SeedException;

class JcrSessionProxy implements InvocationHandler {
    private final JcrSessionLink sessionLink;
    private final String configName;

    private JcrSessionProxy(String configName, JcrSessionLink sessionLink) {
        this.sessionLink = sessionLink;
        this.configName = configName;
    }

    static Session getProxyInstance(String configName, JcrSessionLink sessionLink) {
        return (Session) Proxy.newProxyInstance(JcrSessionProxy.class.getClassLoader(),
                new Class<?>[]{Session.class},
                new JcrSessionProxy(configName, sessionLink));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Session instance = sessionLink.getThreadSession(configName);
        if (instance == null) {
            throw SeedException.createNew(JcrErrorCode.CANNOT_PROVIDE_SESSION).put("config",
                    configName);
        }
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException ex) {
            // Something went wrong during the execution
            // Time to unwrap the cause
            throw ex.getCause();
        }
    }
}

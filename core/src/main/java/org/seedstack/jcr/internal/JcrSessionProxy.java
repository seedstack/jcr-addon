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

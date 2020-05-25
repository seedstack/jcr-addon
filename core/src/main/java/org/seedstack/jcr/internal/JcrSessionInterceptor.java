/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.Session;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jcr.JcrConfig;

public class JcrSessionInterceptor implements MethodInterceptor {

    private final static Map<String, Set<String>> requiredSessions = new ConcurrentHashMap<>();
    private final JcrConfig configuration;
    private final JcrSessionLink sessionLink;

    JcrSessionInterceptor(JcrSessionLink sessionLink, JcrConfig configuration) {
        this.sessionLink = sessionLink;
        this.configuration = configuration;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Set<String> sessions = getRequiredSessions(invocation);

        sessions.forEach(sessionLink::createSession);
        try {
          return invocation.proceed();
        } finally {
          sessions.forEach(sessionLink::finishSession);
        }
    }

    private Set<String> getRequiredSessions(MethodInvocation invocation) {

        Class<?> declaringClass = invocation.getMethod().getDeclaringClass();
        String requestClass = declaringClass.getCanonicalName();
        if (requiredSessions.containsKey(requestClass)) {
            return requiredSessions.get(requestClass);
        }

        List<AnnotatedElement> elements = new ArrayList<>();

        Arrays.stream(declaringClass.getDeclaredFields())
                .filter(Predicates.INJECT_PRESENT)
                .filter(Predicates.FIELD_IS_JCR_SESSION)
                .forEach(elements::add);

        Arrays.stream(declaringClass.getDeclaredConstructors())
                .filter(Predicates.INJECT_PRESENT)
                .map(Constructor::getParameters)
                .flatMap(Arrays::stream)
                .filter(Predicates.PARAMETER_IS_JCR_SESSION)
                .forEach(elements::add);

        Arrays.stream(declaringClass.getDeclaredMethods())
                .filter(Predicates.INJECT_PRESENT)
                .map(Method::getParameters)
                .flatMap(Arrays::stream)
                .filter(Predicates.PARAMETER_IS_JCR_SESSION)
                .forEach(elements::add);

        Set<String> sessions = elements.stream()
                .map(this::readSessionKeyFromElement)
                .distinct()
                .collect(Collectors.toSet());

        requiredSessions.put(requestClass, sessions);
        return sessions;

    }

    private final String readSessionKeyFromElement(AnnotatedElement element) {

        Optional<String> guiceValue = Optional
                .ofNullable(element.getAnnotation(com.google.inject.name.Named.class))
                .map(x -> x.value());
        Optional<String> javaxValue = Optional
                .ofNullable(element.getAnnotation(javax.inject.Named.class))
                .map(x -> x.value());

        if (guiceValue.isPresent()) {
            return guiceValue.get();
        } else if (javaxValue.isPresent()) {
            return javaxValue.get();
        } else {
            return configuration.getDefaultRepository();
        }
    }

    private static class Predicates {

        static final Predicate<AnnotatedElement> INJECT_PRESENT = (
                e) -> (e.isAnnotationPresent(javax.inject.Inject.class)
                        || e.isAnnotationPresent(com.google.inject.Inject.class));

        static final Predicate<Parameter> PARAMETER_IS_JCR_SESSION = (
                e) -> (e.getType().isAssignableFrom(Session.class));

        static final Predicate<Field> FIELD_IS_JCR_SESSION = (
                e) -> (e.getType().isAssignableFrom(Session.class));

    }
}

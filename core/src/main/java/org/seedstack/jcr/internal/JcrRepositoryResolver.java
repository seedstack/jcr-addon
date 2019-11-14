package org.seedstack.jcr.internal;

import java.lang.reflect.Method;

import org.seedstack.shed.reflect.StandardAnnotationResolver;

public class JcrRepositoryResolver
        extends StandardAnnotationResolver<Method, org.seedstack.jcr.JcrRepository> {

    public static final JcrRepositoryResolver INSTANCE = new JcrRepositoryResolver();

    private JcrRepositoryResolver() {
        // No Instances!
    }
}

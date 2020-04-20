package org.seedstack.jcr.fixtures;

import org.seedstack.seed.Install;

import com.google.inject.AbstractModule;

@Install
public class FixtureModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
        bind(TestConstructorFixture.class).to(TestConstructorFixtureImpl.class).asEagerSingleton();

    }

}

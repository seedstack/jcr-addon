package org.seedstack.jcr.fixtures;

import org.junit.runners.model.InitializationError;
import org.seedstack.jcr.BogusFactoryIT;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BogusJUnitRunner extends SeedITRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BogusJUnitRunner.class);

    public BogusJUnitRunner(Class<?> someClass) throws InitializationError {
        super(someClass);

    }

    @Override
    protected Object createTest() {
        BogusFactory.resetCallCount();

        try {
            // Bogus Factory would crash the whole stack.
            // Emulating the initialization of the test is the only way to pass trough this.
            return super.createTest();

        } catch (Exception e) {
            LOGGER.info("CreateTest Failed", e);
        }
        return new BogusFactoryIT();
    }

}

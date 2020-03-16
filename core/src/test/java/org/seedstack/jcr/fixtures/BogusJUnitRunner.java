/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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

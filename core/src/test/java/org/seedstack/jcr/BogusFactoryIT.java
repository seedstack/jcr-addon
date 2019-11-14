/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr;

import javax.jcr.Session;
import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jcr.fixtures.BogusFactory;
import org.seedstack.seed.testing.junit4.SeedITRunner;

import com.google.inject.Inject;

@RunWith(SeedITRunner.class)
public class BogusFactoryIT {

    // Required to perform transaction initialziation mechanisms
    @SuppressWarnings("unused")
    @Inject
    private Session defaultSession;

    @Before
    public void setUp() {
        BogusFactory.resetCallCount();
    }

    @Test
    @Jcr
    @Transactional
    public void testFailure() throws Exception {
        Assertions.assertThat(BogusFactory.getCallCount()).isEqualTo(1);

    }

}

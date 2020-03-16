/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.transaction.Transactional;

import com.google.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.Logging;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.slf4j.Logger;

@RunWith(SeedITRunner.class)
public class SimpleDataRetrievalIT {

    @Inject
    private Session alternativeSession;

    @Inject
    private Session defaultSession;

    @Logging
    private Logger logger;

    @Test
    public void testInjection() throws Exception {
        Assertions.assertThat(defaultSession).isNotNull();
        Assertions.assertThat(alternativeSession).isNotNull();
    }

    @Test
    @Transactional
    public void testNodeModification() throws Exception {
        Node newNode = defaultSession.getRootNode().addNode("test");
        Assertions.assertThat(newNode).isNotNull();
        newNode.setProperty("prop", "jcr-test");
        Assertions.assertThat(defaultSession.getRootNode().getNode("test/").getProperty("prop").getString())
                .isEqualTo("jcr-test");

        logger.info("getnodes {}", defaultSession.getRootNode().getNodes());

        NodeIterator iterator = defaultSession.getRootNode().getNodes();
        while (iterator.hasNext()) {
            logger.info("Removing node {}", iterator.nextNode());
            // iterator.nextNode().remove();
        }
    }
}

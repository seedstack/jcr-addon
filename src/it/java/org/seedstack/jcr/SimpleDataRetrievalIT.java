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
package org.seedstack.jcr;

import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

import com.google.inject.Inject;

@RunWith(SeedITRunner.class)
public class SimpleDataRetrievalIT {

    @Inject
    private Session defaultSession;

    @Inject
    @Named("alternative")
    private Session alternativeSession;

    @Test
    public void testInjection() throws Exception {
        Assertions.assertThat(defaultSession).isNotNull();
        Assertions.assertThat(alternativeSession).isNotNull();
    }

    @Test
    public void testNodeModification() throws Exception {

        Node newNode = defaultSession.getRootNode().addNode("test");
        Assertions.assertThat(newNode).isNotNull();
        newNode.setProperty("prop", "jcr-test");
        Assertions.assertThat(
                defaultSession.getRootNode().getNode("test/").getProperty("prop").getString())
                .isEqualTo("jcr-test");
    }

    public void tearDown() throws Exception {

        NodeIterator iterator = defaultSession.getRootNode().getNodes();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            node.remove();
        }

        defaultSession.save();

    }

}

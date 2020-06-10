/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@WithContentRepository
@RunWith(SeedITRunner.class)
public class ExceptionHandlingIT {

    private static final String REL_PATH = "LEROOOOY-JENKINS";
    private static final String ABS_PATH = "/" + REL_PATH;

    @Inject
    private Session session;

    @Test(expected = PathNotFoundException.class)
    public void testGetNodeIfExists() throws Exception, PathNotFoundException {

        session.getNode(ABS_PATH);
    }

    @Test
    public void testGetNode() throws Exception {
        session.getRootNode().addNode(REL_PATH);
        Node node = session.getNode(ABS_PATH);
        Assertions.assertThat(node).isNotNull();
        Assertions.assertThat(node.getPath()).isNotNull().isEqualTo(ABS_PATH);
        node.remove();
    }
}

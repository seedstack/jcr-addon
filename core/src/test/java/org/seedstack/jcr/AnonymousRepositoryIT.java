/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
public class AnonymousRepositoryIT {

    @Inject
    @Named("anonymous")
    private Session anonymousRepository;

    @Test
    @WithContentRepository
    public void testAnonymousRepository() throws Exception {
        Assertions.assertThat(anonymousRepository).isNotNull();
        Assertions.assertThat(anonymousRepository.isLive()).isTrue();

    }

}

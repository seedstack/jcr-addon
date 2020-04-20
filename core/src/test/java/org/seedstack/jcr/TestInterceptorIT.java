package org.seedstack.jcr;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jcr.fixtures.TestConstructorFixture;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
@WithContentRepository
public class TestInterceptorIT {

    @Inject
    TestConstructorFixture testService;

    @Inject
    private Session defaultSession;

    @Inject
    @Named("anonymous")
    private Session anonSession;

    @Test
    public void testInjector() throws Exception {
        Assertions.assertThat(defaultSession.getRootNode()).isNotNull();
        testService.testSession();
    }

}

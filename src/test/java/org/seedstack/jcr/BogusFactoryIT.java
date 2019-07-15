package org.seedstack.jcr;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jcr.fixtures.BoggusException;
import org.seedstack.jcr.fixtures.BogusITLauncher;
import org.seedstack.seed.testing.LaunchWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
@LaunchWith(value = BogusITLauncher.class)
public class BogusFactoryIT {

    @Test
    public void testFailure() throws Exception {
        Assertions.assertThat(BogusITLauncher.getExceptionMessage())
                .contains(BoggusException.class.getName());
    }

}

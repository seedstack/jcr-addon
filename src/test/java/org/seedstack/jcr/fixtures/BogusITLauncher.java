package org.seedstack.jcr.fixtures;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.seedstack.jcr.fixtures.BogusFactory.CreationBehaviour;
import org.seedstack.seed.core.internal.it.DefaultITLauncher;

public class BogusITLauncher extends DefaultITLauncher {

    private static String exceptionMessage;

    public static String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public void launch(String[] args, Map<String, String> kernelParameters) {
        exceptionMessage = "";
        BogusFactory.setCreationBehaviour(CreationBehaviour.CRASH);
        try {
            super.launch(args, kernelParameters);
        } catch (Exception e) {
            handleException(e);
        } finally {
            BogusFactory.setCreationBehaviour(CreationBehaviour.NULL);
        }

    }

    private static void handleException(Exception e) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            e.printStackTrace(new PrintStream(baos));
            exceptionMessage = baos.toString();
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }

    }

}

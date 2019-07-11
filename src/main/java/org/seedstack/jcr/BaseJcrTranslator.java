package org.seedstack.jcr;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.spi.JcrTranslator;

public abstract class BaseJcrTranslator implements JcrTranslator {

    @Override
    public Map<String, String> translate(RepositoryConfig config) {
        Map<String, String> transaltedConfig = new HashMap<>();
        for (Entry<Object, Object> property : config.getVendorProperties().entrySet()) {
            transaltedConfig.put(property.getKey().toString(), property.getValue().toString());
        }
        return transaltedConfig;
    }

}

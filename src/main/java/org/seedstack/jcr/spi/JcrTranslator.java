/**
 * 
 */
package org.seedstack.jcr.spi;

import java.util.Map;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;

/**
 * @author xiabou
 *
 */
public interface JcrTranslator {

    Map<String, String> translate(RepositoryConfig config);

}

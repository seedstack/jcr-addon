/** */
package org.seedstack.jcr;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

/** This qualifiers marks the use of the Jcr Repository persistence */
@Documented
@Retention(RUNTIME)
@Qualifier
public @interface Jcr {
  // Empty Interface
}

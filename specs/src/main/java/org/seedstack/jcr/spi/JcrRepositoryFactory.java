/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.spi;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig.RepositoryConfig;

public interface JcrRepositoryFactory {

    Session createSession(RepositoryConfig configuration) throws RepositoryException;

    Map<String, String> translateConfiguration(RepositoryConfig config);

}

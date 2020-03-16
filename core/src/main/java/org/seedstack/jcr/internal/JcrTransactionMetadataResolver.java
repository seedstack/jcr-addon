/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.RepositoryConfig;
import org.seedstack.jcr.JcrRepository;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

class JcrTransactionMetadataResolver implements TransactionMetadataResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(JcrTransactionMetadataResolver.class);

	@Inject
	private Application application;

	@Override
	public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {

		if (JcrTransactionMetadataResolver.class.equals(defaults.getHandler())) {
			return null;
		}
		LOGGER.trace("Resolving Jcr metadata for {}", methodInvocation.getMethod());
		JcrConfig config = application.getConfiguration().get(JcrConfig.class);

		String repositoryKey = JcrRepositoryResolver.INSTANCE.apply(methodInvocation.getMethod())
				.map(JcrRepository::value).orElse(config.getDefaultRepository());

		LOGGER.trace("Resolved repository key '{}'", repositoryKey);

		if (Strings.isNullOrEmpty(repositoryKey)) {
			throw SeedException.createNew(JcrErrorCode.NO_JCR_REPOSITORY_SPECIFIED_FOR_TRANSACTION).put("method",
					methodInvocation.getMethod().toString());
		}

		if (!config.getRepositories().containsKey(repositoryKey)) {
			throw SeedException.createNew(JcrErrorCode.NO_JCR_CONFIGURATION_AVAILABLE)
					.put("method", methodInvocation.getMethod().toString()).put("config", repositoryKey);

		}

		RepositoryConfig repositoryConfig = config.getRepositories().get(repositoryKey);
		TransactionMetadata metadata = new TransactionMetadata();

		metadata.setHandler(JcrTransactionHandler.class);
		metadata.setExceptionHandler(repositoryConfig.getExceptionHandler());
		metadata.setResource(repositoryKey);

		LOGGER.trace("Resolved metadata '{}'", metadata);

		return metadata;
	}
}

/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.spi;

import javax.jcr.Session;

import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

/**
 * Exception handler for JMS transactions.
 */
public interface JcrExceptionHandler extends ExceptionHandler<Session> {

    @Override
    boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata,
            Session associatedTransaction);

}

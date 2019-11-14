/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import org.seedstack.shed.exception.ErrorCode;

enum JcrErrorCode implements ErrorCode {
    CANNOT_CREATE_FACTORY, CANNOT_CREATE_SESSION, NO_JCR_REPOSITORY_SPECIFIED_FOR_TRANSACTION, NO_JCR_CONFIGURATION_AVAILABLE, ACCESSING_JCR_SESSION_OUTSIDE_TRANSACTION, SESSION_SAVE_COULD_NOT_BE_EXECUTED, SESSION_CANNOT_BE_RESTORED
}

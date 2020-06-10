/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jcr.internal;

import org.seedstack.shed.exception.ErrorCode;

enum JcrErrorCode implements ErrorCode {
    CANNOT_CREATE_FACTORY,
    CANNOT_CREATE_SESSION,
    CANNOT_LOCATE_PROVIDER,
    CANNOT_PROVIDE_SESSION,
    SESSION_ALREADY_DISPOSED,
    SESSION_DISPOSAL_FAILED
}

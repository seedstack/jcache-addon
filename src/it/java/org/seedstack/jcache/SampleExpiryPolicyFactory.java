/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcache;

import javax.cache.configuration.Factory;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

public class SampleExpiryPolicyFactory implements Factory<ExpiryPolicy> {
    private static final long serialVersionUID = 8279284252845466109L;

    @Override
    public ExpiryPolicy create() {
        return new ExpiryPolicy() {
            @Override
            public Duration getExpiryForCreation() {
                return Duration.ETERNAL;
            }

            @Override
            public Duration getExpiryForAccess() {
                return Duration.ETERNAL;
            }

            @Override
            public Duration getExpiryForUpdate() {
                return Duration.ETERNAL;
            }
        };
    }
}

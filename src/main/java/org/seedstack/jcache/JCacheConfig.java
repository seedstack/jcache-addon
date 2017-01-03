/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcache;

import org.seedstack.coffig.Config;

import javax.cache.configuration.Factory;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.Map;

@Config("jcache")
public class JCacheConfig {

    private Class<? extends CachingProvider> defaultProvider;
    private Map<String, CacheConfig> caches = new HashMap<>();

    public Class<? extends CachingProvider> getDefaultProvider() {
        return defaultProvider;
    }

    public JCacheConfig setDefaultProvider(Class<? extends CachingProvider> defaultProvider) {
        this.defaultProvider = defaultProvider;
        return this;
    }

    public Map<String, CacheConfig> getCaches() {
        return caches;
    }

    public JCacheConfig addCache(String name, CacheConfig config) {
        this.caches.put(name, config);
        return this;
    }

    public static class CacheConfig {
        private Class<? extends CachingProvider> provider;
        private ExpiryPolicy expiryPolicy;
        private long expiryDuration = 900;
        private Class<? extends Factory<? extends javax.cache.expiry.ExpiryPolicy>> expiryPolicyFactory;

        public Class<? extends CachingProvider> getProvider() {
            return provider;
        }

        public CacheConfig setProvider(Class<? extends CachingProvider> provider) {
            this.provider = provider;
            return this;
        }

        public ExpiryPolicy getExpiryPolicy() {
            return expiryPolicy;
        }

        public CacheConfig setExpiryPolicy(ExpiryPolicy expiryPolicy) {
            this.expiryPolicy = expiryPolicy;
            return this;
        }

        public long getExpiryDuration() {
            return expiryDuration;
        }

        public CacheConfig setExpiryDuration(long expiryDuration) {
            this.expiryDuration = expiryDuration;
            return this;
        }

        public Class<? extends Factory<? extends javax.cache.expiry.ExpiryPolicy>> getExpiryPolicyFactory() {
            return expiryPolicyFactory;
        }

        public CacheConfig setExpiryPolicyFactory(Class<? extends Factory<? extends javax.cache.expiry.ExpiryPolicy>> expiryPolicyFactory) {
            this.expiryPolicyFactory = expiryPolicyFactory;
            return this;
        }
    }

    /**
     * Enumerates all built-in expiry policies.
     */
    public enum ExpiryPolicy {
        /**
         * An {@link javax.cache.expiry.ExpiryPolicy} that defines the expiry {@link Duration}
         * of a Cache Entry based on when it was last touched. A touch includes
         * creation, update or access.
         */
        TOUCHED(TouchedExpiryPolicy.class, true),

        /**
         * An {@link javax.cache.expiry.ExpiryPolicy} that defines the expiry {@link Duration}
         * of a Cache Entry based on the last time it was accessed. Accessed
         * does not include a cache update.
         */
        ACCESSED(AccessedExpiryPolicy.class, true),

        /**
         * An {@link javax.cache.expiry.ExpiryPolicy} that defines the expiry {@link Duration}
         * of a Cache Entry based on when it was created. An update does not reset
         * the expiry time.
         */
        CREATED(CreatedExpiryPolicy.class, true),

        /**
         * An eternal {@link javax.cache.expiry.ExpiryPolicy} specifies that Cache Entries
         * won't expire.  This however doesn't mean they won't be evicted if an
         * underlying implementation needs to free-up resources where by it may
         * choose to evict entries that are not due to expire.
         */
        ETERNAL(EternalExpiryPolicy.class, false),

        /**
         * An {@link javax.cache.expiry.ExpiryPolicy} that defines the expiry {@link Duration}
         * of a Cache Entry based on the last time it was updated. Updating
         * includes created and changing (updating) an entry.
         */
        MODIFIED(ModifiedExpiryPolicy.class, true);

        private static final String FACTORY_OF = "factoryOf";
        private final Class<? extends javax.cache.expiry.ExpiryPolicy> expiryPolicyClass;
        private final boolean hasDuration;

        ExpiryPolicy(Class<? extends javax.cache.expiry.ExpiryPolicy> expiryPolicyClass, boolean hasDuration) {
            this.expiryPolicyClass = expiryPolicyClass;
            this.hasDuration = hasDuration;
        }

        @SuppressWarnings("unchecked")
        public Factory<javax.cache.expiry.ExpiryPolicy> getFactory(Duration duration) {
            try {
                if (hasDuration) {
                    return (Factory<javax.cache.expiry.ExpiryPolicy>) this.expiryPolicyClass.getDeclaredMethod(FACTORY_OF, Duration.class).invoke(null, duration);
                } else {
                    return (Factory<javax.cache.expiry.ExpiryPolicy>) this.expiryPolicyClass.getDeclaredMethod(FACTORY_OF).invoke(null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to create expiry policy " + expiryPolicyClass.getCanonicalName(), e);
            }
        }
    }
}

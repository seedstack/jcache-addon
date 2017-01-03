/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcache.internal;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.PluginException;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.seedstack.jcache.JCacheConfig;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This plugin provides JSR-107 jcache integration.
 */
public class CachePlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachePlugin.class);

    private final Map<String, Cache> caches = new HashMap<String, Cache>();

    @Override
    public String name() {
        return "jcache";
    }

    @Override
    @SuppressWarnings("unchecked")
    public InitState initialize(InitContext initContext) {
        JCacheConfig jCacheConfig = getConfiguration(JCacheConfig.class);

        Class<? extends CachingProvider> defaultProvider = jCacheConfig.getDefaultProvider();

        if (defaultProvider != null) {
            LOGGER.debug("Caching default provider is configured to {}", defaultProvider);
        } else {
            LOGGER.debug("Caching default provider is not specified and will be autodetected from classpath");
        }

        for (Map.Entry<String, JCacheConfig.CacheConfig> cacheEntry : jCacheConfig.getCaches().entrySet()) {
            String cacheName = cacheEntry.getKey();
            JCacheConfig.CacheConfig cacheConf = cacheEntry.getValue();
            MutableConfiguration cacheConfiguration = new MutableConfiguration();

            // Expiry policy
            Class<? extends Factory<? extends ExpiryPolicy>> expiryPolicyFactory = cacheConf.getExpiryPolicyFactory();
            JCacheConfig.ExpiryPolicy expiryPolicy = cacheConf.getExpiryPolicy();
            Long expiryDuration = cacheConf.getExpiryDuration();

            if (expiryPolicyFactory != null) {
                try {
                    cacheConfiguration.setExpiryPolicyFactory(expiryPolicyFactory.newInstance());
                } catch (Exception e) {
                    throw new PluginException("Unable to instantiate custom expiry policy factory " + expiryPolicyFactory, e);
                }
            } else if (expiryPolicy != null) {
                try {
                    cacheConfiguration.setExpiryPolicyFactory(expiryPolicy.getFactory(new Duration(TimeUnit.SECONDS, expiryDuration)));
                } catch (Exception e) {
                    throw new PluginException("Unable to instantiate built-in expiry policy " + expiryPolicy, e);
                }
            }

            Class<? extends CachingProvider> cacheProvider = cacheConf.getProvider();
            if (cacheProvider == null && defaultProvider != null) {
                cacheProvider = defaultProvider;
            }
            if (cacheProvider == null) {
                LOGGER.trace("Configuring jcache {} with autodetected provider", cacheName);
                caches.put(cacheName, Caching.getCachingProvider().getCacheManager().createCache(cacheName, cacheConfiguration));
            } else {
                LOGGER.trace("Configuring jcache {} with provider {}", cacheName, cacheProvider.getName());
                caches.put(cacheName, Caching.getCachingProvider(cacheProvider.getName()).getCacheManager().createCache(cacheName, cacheConfiguration));
            }
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return new CacheModule(this.caches);
    }

    @Override
    public void stop() {
        LOGGER.info("Destroying caches");
        Caching.getCachingProvider().close();
    }
}

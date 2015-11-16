---
title: "Overview"
addon: "JCache"
repo: "https://github.com/seedstack/jcache-addon"
author: "SeedStack"
min-version: "15.11+"
backend: true
menu:
    JCacheAddon:
        weight: 10
---

The JCache add-on integrates the JCache API (a.k.a. JSR 107) which allows to interact with compliant caching providers
in a declarative or a programmatic way.

**Implementations are not provided by this add-on and must be configured depending on your caching solution**.

{{< dependency g="org.seedstack.addons.jcache" a="jcache" >}}

JCache specification jar dependency is required as well:

    <dependency>
        <groupId>javax.cache</groupId>
        <artifactId>cache-api</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>

The full specification PDF can be found [here](http://download.oracle.com/otn-pub/jcp/jcache-1_0-fr-eval-spec/JSR107FinalSpecification.pdf).

# Configuration

You must define the cache(s) you will use in your application in the configuration:

    [org.seedstack.jcache]
    caches = myCache1, myCache2, ...

You can then further configure each cache specifically by using the `org.seedstack.jcache.cache` prefix followed by
the cache name:

    [org.seedstack.jcache.cache.myCache1]
    ...

## Cache provider

If you have exactly one compliant cache provider in the classpath, it will be automatically picked by the cache support.
Otherwise you must specify which provider to use for each cache:

    [org.seedstack.jcache.cache.myCache1]
    provider = fully.qualified.classname.of.caching.Provider

You can also specify a global default provider, which will be picked for every cache without an explicitly specified
provider:

    [org.seedstack.jcache]
    default-provider = fully.qualified.classname.of.default.caching.Provider


## Expiry policy factory

You can specify a custom expiry policy factory for each cache:

    [org.seedstack.jcache]
    expiry-policy-factory = fully.qualified.classname.of.expiry.policy.Factory

An expiry policy factory must implement `javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy>`.

# Usage

You must define the cache(s) you will use in your application in the configuration:

    [org.seedstack.jcache]
    caches = myCache1, myCache2, ...

You can then further configure each cache specifically by using the `org.seedstack.jcache.cache` prefix followed by
the cache name:

    [org.seedstack.jcache.cache.myCache1]
    ...

## Cache provider

If you have exactly one compliant cache provider in the classpath, it will be automatically picked by the cache support.
Otherwise you must specify which provider to use for each cache:

    [org.seedstack.jcache.cache.myCache1]
    provider = fully.qualified.classname.of.caching.Provider

You can also specify a global default provider, which will be picked for every cache without an explicitly specified
provider:

    [org.seedstack.jcache]
    default-provider = fully.qualified.classname.of.default.caching.Provider


## Expiry policy factory

You can specify a custom expiry policy factory for each cache:

    [org.seedstack.jcache]
    expiry-policy-factory = fully.qualified.classname.of.expiry.policy.Factory

An expiry policy factory must implement `javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy>`.

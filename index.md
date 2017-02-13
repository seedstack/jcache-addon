---
Title: "JCache"
repo: "https://github.com/seedstack/jcache-addon"
description: "Integration of the JCache standard which provides easy-to-use applicative caching."
author: Emmanuel VINEL
tags:
    - "cache"
zones:
    - Addons
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

The JCache specification provides an annotation-based declarative API and a programmatic API.
  
## Declarative API

The declarative API is annotation-based and can be used on methods. When the method will be invoked, the corresponding
cache operation will take place.

### Cache a return value

The following code caches the return value of the method, using its arguments as a key:

    @CacheResult(cacheName = "things")
    public Thing getThing(String scope, String name) {
        ...
    }
    
Keys generation can be customized using the `CacheKeyGenerator` interface. If no specific implementation is specified, 
the default implementation, per spec, takes all parameters unless one or more parameters are annotated with the @CacheKey 
annotation, in which case only those are used:
    
    @CacheResult(cacheName = "things")
    public Thing getThing(@CacheKey String scope, @CacheKey String name, Date timestamp) {
        ...
    }
    
`@CacheResult` brings the concept of exception cache: whenever a method execution failed, it is possible to cache the 
exception that was raised to prevent calling the method again.

JCache has the notion of `CacheResolver` which permits to resolve the cache to use at runtime.
 
    @CacheResult(cacheName = "things", cacheResolverFactory = MyCacheResolverFactory.class)
    public Thing getThing(@CacheKey String scope, @CacheKey String name, Date timestamp) {
        ...
    }
    
Finally, `@CacheResult` has a skipGet attribute that can be enabled to always invoke the method regardless of the status 
of the cache. This is useful for create or update methods that should always be executed and have their returned value 
placed in the cache.

### Put a value

The following code add `updatedThing` to the `things` cache with the `scope` and `name` arguments as the key:

    @CachePut(cacheName = "things")
    public void updateThing(String scope, String name, @CacheValue updatedThing) {
        ...
    }

`@CacheValue` annotated parameters are automatically excluded from key generation.

As for `@CacheResult`, `@CachePut` allows to manage any exception that is thrown while executing the method, preventing the 
put operation to happen if the thrown exception matches the filter specified on the annotation.

Finally, it is possible to control if the cache is updated before or after the invocation of the annotated method. Of 
course, if it is updated before, no exception handling takes place.

### Remove a value

The following code remove the entry with the `scope` and `name` arguments as the key from the `things` cache:

    @CacheRemove(cacheName = "things")
    public void deleteThing(String scope, String name) {
        ...
    }
    
`@CacheRemove` has a special exception handling to prevent the eviction if the annotated method throws an exception that 
matches the filter specified on the annotation.

### Remove all values

The following code remove all entries from the `things` cache:

    @CacheRemoveAll(cacheName = "things")
    public void clearAllThings() {
        ...
    }

### Cache defaults

`@CacheDefaults` is a class-level annotation that allows you to share common settings on any caching operation defined 
on the class. These are:

* The name of the cache
* The custom CacheResolverFactory
* The custom CacheKeyGenerator
    
## Programmatic API

If you need a more fine-grained control of your caches, you can also use the programmatic API. You just need to inject
the needed `Cache` object(s):

    @Inject @Named("things")
    private Cache thingsCache;
    
Please check the JavaDoc of the `javax.cache.Cache` interface for more information.

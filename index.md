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
in a declarative or a programmatic way.<!--more-->

# Dependencies

The add-on dependency is:

{{< dependency g="org.seedstack.addons.jcache" a="jcache" >}}

An JCache-compliant implementation is also required, such as the popular [EhCache](http://www.ehcache.org/):

{{< dependency g="org.ehcache" a="ehcache" >}}


# Configuration

Configuration is done by declaring one or more caches:

{{% config p="jcache" %}}
```yaml
jcache:
  # Configured caches with the name of the cache as key
  caches:
    cache1:
      # The JCache provider to use (fallback to global default if not specified)
      provider: (Class<? extends CachingProvider>)
      
      # The built-in expiry policy to use 
      expiryPolicy: (TOUCHED|ACCESSED|CREATED|MODIFIED|ETERNAL)
      
      # Expiry duration in seconds for built-in expiry policies (defaults to 900)
      expiryDuration: (int)
      
      # The factory of the custom expiry policy to use instead of the built-in expiry policy 
      expiryPolicyFactory: (Class<? extends Factory<? extends javax.cache.expiry.ExpiryPolicy>>)
    
  # The default JCache provider to use if not specified on a cache (auto-detected if not specified at all)
  defaultProvider: (Class<? extends CachingProvider>) 
```
{{% /config %}}    

# Declarative usage

The declarative API is annotation-based and can be used on methods. When the method will be invoked, the corresponding
cache operation will take place.

## Cache a return value

The following code caches the return value of the method, using its arguments as a key:

```java
public class SomeClass {
    @CacheResult(cacheName = "things")
    public Thing getThing(String scope, String name) {
        // do something
    }
}
```
    
Keys generation can be customized using the {{< java "javax.cache.annotation.CacheKeyGenerator" >}} interface. If no specific implementation is specified, 
the default implementation, per spec, takes all parameters unless one or more parameters are annotated with the 
{{< java "javax.cache.annotation.CacheKey" "@" >}} annotation, in which case only those are used:
    
```java
public class SomeClass {
    @CacheResult(cacheName = "things")
    public Thing getThing(@CacheKey String scope, @CacheKey String name, Date timestamp) {
        // do something
    }
}
```

{{< java "javax.cache.annotation.CacheResult" "@" >}} brings the concept of exception cache: whenever a method execution failed, 
it is possible to cache the exception that was raised to prevent calling the method again.

JCache has the notion of `CacheResolver` which permits to resolve the cache to use at runtime.
 
```java
public class SomeClass {
    @CacheResult(cacheName = "things", cacheResolverFactory = MyCacheResolverFactory.class)
    public Thing getThing(@CacheKey String scope, @CacheKey String name, Date timestamp) {
        // do something
    }
}
```
    
Finally, {{< java "javax.cache.annotation.CacheResult" "@" >}} has a skipGet attribute that can be enabled to always invoke the method regardless of the status 
of the cache. This is useful for create or update methods that should always be executed and have their returned value 
placed in the cache.

## Put a value

The following code add `updatedThing` to the `things` cache with the `scope` and `name` arguments as the key:

```java
public class SomeClass {
    @CachePut(cacheName = "things")
    public void updateThing(String scope, String name, @CacheValue updatedThing) {
        // do something
    }
}
```

{{< java "javax.cache.annotation.CacheValue" "@" >}} annotated parameters are automatically excluded from key generation.

Same as {{< java "javax.cache.annotation.CacheResult" "@" >}}, {{< java "javax.cache.annotation.CachePut" "@" >}} allows 
to manage any exception that is thrown while executing the method, preventing the put operation to happen if the thrown 
exception matches the filter specified on the annotation.

Finally, it is possible to control if the cache is updated before or after the invocation of the annotated method. Of 
course, if it is updated before, no exception handling takes place.

## Remove a value

The {{< java "javax.cache.annotation.CacheRemove" "@" >}} annotation removes the entry with the `scope` and `name` 
arguments as the key from the `things` cache:

```java
public class SomeClass {
    @CacheRemove(cacheName = "things")
    public void deleteThing(String scope, String name) {
        // do something
    }
}
```

This annotation has a special exception handling to prevent the eviction if the annotated method throws an exception that 
matches the filter specified on the annotation.

## Remove all values

The {{< java "javax.cache.annotation.CacheRemoveAll" "@" >}} annotation removes all entries from the `things` cache:

```java
public class SomeClass {
    @CacheRemoveAll(cacheName = "things")
    public void clearAllThings() {
        // do something
    }
}
```

## Cache defaults

{{< java "javax.cache.annotation.CacheDefaults" "@" >}} is a class-level annotation that allows you to share common 
settings on any caching operation defined on the class. These are:

* The name of the cache
* The custom CacheResolverFactory
* The custom CacheKeyGenerator
    
# Programmatic usage

If you need a more fine-grained control of your caches, you can also use the programmatic API. You just need to inject
the needed `Cache` object(s):

```java
public class SomeClass {
    @Inject 
    @Named("things")
    private Cache thingsCache;
}
```

Please check the JavaDoc of the {{< java "javax.cache.Cache" >}} interface for more information.

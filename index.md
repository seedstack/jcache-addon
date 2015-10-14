---
title: "Overview"
addon: "JCache"
repo: "https://github.com/seedstack/jcache-addon"
author: "SeedStack"
min-version: "15.7+"
menu:
    JCacheAddon:
        weight: 10
---

The JCache add-on integrates the JCache API (a.k.a. JSR 107) which allows to interact with compliant caching providers
in a declarative or a programmatic way.

**Implementations are not provided by this add-on and must be configured depending on your caching solution**.

To enable the JCache add-on in your application, use the following dependency snippet:

    <dependency>
        <groupId>org.seedstack.addons</groupId>
        <artifactId>jcache</artifactId>
    </dependency>

JCache specification jar dependency is required as well:

    <dependency>
        <groupId>javax.cache</groupId>
        <artifactId>cache-api</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>

The full specification PDF can be found [here](http://download.oracle.com/otn-pub/jcp/jcache-1_0-fr-eval-spec/JSR107FinalSpecification.pdf).
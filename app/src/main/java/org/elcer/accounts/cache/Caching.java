package org.elcer.accounts.cache;

import org.elcer.cache.Cache;
import org.elcer.cache.CacheSupport;
import org.elcer.cache.IgniteCacheSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


@Service
@Profile("!test")
public class Caching {

    private CacheSupport cacheSupport;


    @Inject
    private ApplicationContext applicationContext;


    @Value("${cache.url:localhost:10800}")
    private String url;


    public Cache<Object, Object> getOrCreateCache(String name) {
        if (cacheSupport == null) {
            throw new RuntimeException("Cache is not initialized");
        }
        return cacheSupport.getOrCreateCache(name);
    }

    @PostConstruct
    private void init() {
        CacheInterceptorImpl.APPLICATION_CONTEXT = applicationContext;
        cacheSupport = new IgniteCacheSupport(url);
    }


}

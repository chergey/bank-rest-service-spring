package org.elcer.accounts.cache;

import org.elcer.cache.Cache;
import org.elcer.cache.CacheSupport;
import org.elcer.cache.IgniteCacheSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
@Profile("!test")
public class Caching {

    private static CacheSupport cacheSupport;


    @Value("${cache.url}:localhost:10800")
    private String url;


    public static Cache<Object, Object> getOrCreateCache(String name) {
        if (cacheSupport == null) {
            throw new RuntimeException("Cache is not initialized");
        }
        return cacheSupport.getOrCreateCache(name);
    }

    @PostConstruct
    private void init() {
        cacheSupport = new IgniteCacheSupport();
        cacheSupport.connect(url);
    }


}

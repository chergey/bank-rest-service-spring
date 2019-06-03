package org.elcer.accounts.cache;

import org.elcer.cache.CacheCreator;
import org.elcer.cache.ignite.IgniteCacheCreator;
import org.elcer.eclipselink.cache.DefaultCacheSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


@Service
@Profile("!test")
public class AccountCacheSupport extends DefaultCacheSupport {
    private CacheCreator cacheCreator;

    @Inject
    private ApplicationContext applicationContext;

    @Value("${cache.url:localhost:10800}")
    private String url;

    @Override
    protected CacheCreator getCacheCreator() {
        return cacheCreator;
    }

    @PostConstruct
    private void init() {
        AccountCacheInterceptor.setApplicationContext(applicationContext);
        cacheCreator = new IgniteCacheCreator(url);
    }
}

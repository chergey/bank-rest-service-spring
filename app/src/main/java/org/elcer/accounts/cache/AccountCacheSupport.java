package org.elcer.accounts.cache;

import org.elcer.cache.CacheCreator;
import org.elcer.cache.ignite.IgniteCacheCreator;
import org.elcer.eclipselink.cache.DefaultCacheSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("accountCacheSupport")
@Profile("!test")
public class AccountCacheSupport extends DefaultCacheSupport {

    private static final Logger logger = LoggerFactory.getLogger(AccountCacheSupport.class);

    private CacheCreator cacheCreator;

    @Value("${cache.url:localhost:10800}")
    private String url;

    @Override
    protected CacheCreator getCacheCreator() {
        return cacheCreator;
    }

    @PostConstruct
    private void init() {
        logger.info("AccountCacheSupport init");
        AccountCacheInterceptor.setCacheSupport(this);
        cacheCreator = new IgniteCacheCreator(url);
    }
}

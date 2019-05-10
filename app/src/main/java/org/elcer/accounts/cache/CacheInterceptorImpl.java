package org.elcer.accounts.cache;


import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;
import org.elcer.accounts.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class CacheInterceptorImpl extends CacheInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CacheInterceptorImpl.class);

    public static ApplicationContext APPLICATION_CONTEXT;

    private static final String CACHE_NAME = Account.class.getName();

    private Caching caching;

    public CacheInterceptorImpl(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession);

        caching = APPLICATION_CONTEXT.getBean(Caching.class);
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    protected CacheKeyInterceptor createCacheKeyInterceptor(CacheKey wrappedCacheKey) {

        final long longKey = (long) wrappedCacheKey.getKey();

        CacheKeyInterceptor newKey = new CacheKeyInterceptor(wrappedCacheKey) {
            @Override
            public Object getObject() {
                logger.info("CacheKeyInterceptor.getObject {}", longKey);
                return caching.getOrCreateCache(CACHE_NAME).get(longKey);
            }

            @Override
            public void setObject(Object object) {
                logger.info("CacheKeyInterceptor.setObject {}", object);
                caching.getOrCreateCache(CACHE_NAME).put(longKey, object);
            }
        };

        logger.info("createCacheKeyInterceptor");
        return newKey;

    }

    @Override
    public boolean containsKey(Object primaryKey) {
        return caching.getOrCreateCache(CACHE_NAME).containsKey(primaryKey);
    }

    @Override
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        return null;
    }

    @Override
    public Map<Object, CacheKey> getAllCacheKeysFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        return null;
    }

    @Override
    public void release() {

    }


}
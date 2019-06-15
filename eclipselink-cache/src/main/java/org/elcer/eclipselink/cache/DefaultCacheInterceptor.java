package org.elcer.eclipselink.cache;


import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class DefaultCacheInterceptor extends CacheInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCacheInterceptor.class);

    private final DefaultCacheSupport cacheSupport;
    private final String cacheName;

    public DefaultCacheInterceptor(IdentityMap targetIdentityMap, AbstractSession interceptedSession,
                                   String cacheName, DefaultCacheSupport cacheSupport) {
        super(targetIdentityMap, interceptedSession);

        if (cacheSupport == null) {
            throw new RuntimeException("Cache support is not initialized");
        }

        this.cacheSupport = cacheSupport;
        this.cacheName = cacheName;
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
                return cacheSupport.getOrCreateCache(cacheName).get(longKey);
            }

            @Override
            public void setObject(Object object) {
                logger.info("CacheKeyInterceptor.setObject {}", object);
                cacheSupport.getOrCreateCache(cacheName).put(longKey, object);
            }
        };

        logger.info("createCacheKeyInterceptor");
        return newKey;

    }

    @Override
    public boolean containsKey(Object primaryKey) {
        return cacheSupport.getOrCreateCache(cacheName).containsKey(primaryKey);
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
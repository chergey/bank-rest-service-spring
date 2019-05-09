package org.elcer.accounts.cache;


import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;

import java.util.Map;

public class IgniteCacheInterceptor extends CacheInterceptor {

    private static final String CACHE_NAME = "AccountsCache";

    public IgniteCacheInterceptor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession);
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
                return Caching.getOrCreateCache(CACHE_NAME).get(longKey);
            }

            @Override
            public void setObject(Object object) {
                Caching.getOrCreateCache(CACHE_NAME).put(longKey, object);
            }
        };
        return newKey;

    }

    @Override
    public boolean containsKey(Object primaryKey) {
        return Caching.getOrCreateCache(CACHE_NAME).containsKey(primaryKey);
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
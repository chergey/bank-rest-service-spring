package org.elcer.accounts.cache;


import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.elcer.accounts.model.Account;
import org.elcer.eclipselink.cache.DefaultCacheInterceptor;

public class AccountCacheInterceptor extends DefaultCacheInterceptor {

    private static AccountCacheSupport CACHE_SUPPORT;

    private static final String CACHE_NAME = Account.class.getName();

    public AccountCacheInterceptor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession, CACHE_NAME, CACHE_SUPPORT);
    }

    static void setCacheSupport(AccountCacheSupport cacheSupport) {
        CACHE_SUPPORT = cacheSupport;
    }
}
package org.elcer.accounts.cache;


import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.elcer.accounts.model.Account;
import org.elcer.eclipselink.cache.DefaultCacheInterceptor;
import org.springframework.context.ApplicationContext;

public class AccountCacheInterceptor extends DefaultCacheInterceptor {

    private static ApplicationContext APPLICATION_CONTEXT;

    private static final String CACHE_NAME = Account.class.getName();

    public AccountCacheInterceptor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession, CACHE_NAME,
                APPLICATION_CONTEXT.getBean(AccountCacheSupport.class));
    }

    static void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }
}
package org.elcer.accounts.db;

import org.elcer.accounts.cache.CacheCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Map;


@Profile("!test")
@DependsOn("accountCacheSupport")
public class CacheSupportedConfiguration extends EclipseLinkJpaConfiguration {
    protected CacheSupportedConfiguration(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        Map<String, Object> vendorProperties = super.getVendorProperties();
        vendorProperties.put("eclipselink.descriptor.customizer.Account", CacheCustomizer.class.getName());
        return vendorProperties;
    }

}

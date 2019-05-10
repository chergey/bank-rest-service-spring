package org.elcer.accounts.db;

import com.google.common.collect.Lists;
import org.elcer.accounts.cache.CacheCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("eclipselink")
public class EclipseLinkJpaConfiguration extends JpaBaseConfiguration {

    @Inject
    private Environment environment;

    protected EclipseLinkJpaConfiguration(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
    }


    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipselinkEarlyTranAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        var map = new HashMap<String, Object>();
        map.put("eclipselink.weaving", detectWeavingMode());
        map.put("eclipselink.ddl-generation", "drop-and-create-tables");
        ArrayList<String> activeProfiles = Lists.newArrayList(environment.getActiveProfiles());
        if (!activeProfiles.contains("test")) {
            map.put("eclipselink.descriptor.customizer.Account", CacheCustomizer.class.getName());
        }
        return map;
    }

    private String detectWeavingMode() {
        return InstrumentationLoadTimeWeaver.isInstrumentationAvailable() ? "true" : "static";
    }


}
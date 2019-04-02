package org.elcer.accounts.app;

import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

public class EclipselinkEarlyTranAdapter extends EclipseLinkJpaVendorAdapter {
    private final EclipseLinkJpaDialect eclipseLinkJpaDialect = new EclipselinkEarlyTranDialect();

    @Override
    public EclipseLinkJpaDialect getJpaDialect() {
        return eclipseLinkJpaDialect;
    }
}

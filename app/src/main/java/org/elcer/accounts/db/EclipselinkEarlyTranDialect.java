package org.elcer.accounts.db;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.TransactionDefinition;

import javax.persistence.EntityManager;
import java.sql.SQLException;

public class EclipselinkEarlyTranDialect extends EclipseLinkJpaDialect {

    @Override
    public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition) throws SQLException {
        Object res = super.beginTransaction(entityManager, definition);
        if (!definition.isReadOnly()) {
            UnitOfWork uow = entityManager.unwrap(UnitOfWork.class);
            AbstractSession clientSession = uow.getParent();
            if (clientSession instanceof ClientSession) {
                AbstractSession serverSession = clientSession.getParent();
                if (serverSession instanceof ServerSession)
                    ((ServerSession) serverSession).acquireClientConnection((ClientSession) clientSession);
            }

        }

        return res;
    }
}

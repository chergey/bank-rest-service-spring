package org.elcer.accounts.app;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.SQLException;

public class EclipselinkEarlyTranDialect extends EclipseLinkJpaDialect {

    @Override
    public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {
        super.beginTransaction(entityManager, definition);
        if (!definition.isReadOnly()) {

            ServerSession serverSession = null;
            ClientSession clientSession = null;

            UnitOfWork unitOfWork = entityManager.unwrap(UnitOfWork.class);
            AbstractSession unit = unitOfWork.getParent();
            while (unit != null) {
                if (unit instanceof ServerSession) {
                    serverSession = ((ServerSession) unit);
                    break;
                }
                if (unit instanceof ClientSession) {
                    clientSession = ((ClientSession) unit);
                }
                unit = unit.getParent();

            }

            if (serverSession == null || clientSession == null) {
                throw new RuntimeException("No server/client session defined!");
            }

            serverSession.acquireClientConnection(clientSession);
        }

        return null;
    }
}

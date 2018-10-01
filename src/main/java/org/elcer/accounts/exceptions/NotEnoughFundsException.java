package org.elcer.accounts.exceptions;

public class NotEnoughFundsException extends AccountException {

    public NotEnoughFundsException(long accountId) {
        super(accountId);
    }
}

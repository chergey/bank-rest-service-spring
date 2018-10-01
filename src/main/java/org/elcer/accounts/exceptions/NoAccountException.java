package org.elcer.accounts.exceptions;


public class NoAccountException extends AccountException {

    public NoAccountException(long accountId) {
        super(accountId);
    }
}

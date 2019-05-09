package org.elcer.accounts.exceptions;


public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(long accountId) {
        super(accountId);
    }
}

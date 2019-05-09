package org.elcer.accounts.exceptions;

class AccountException extends RuntimeException {

    private long accountId;

    AccountException(long accountId) {
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}

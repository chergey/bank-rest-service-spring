package org.elcer.accounts.services;


import org.elcer.accounts.exceptions.AccountNotFoundException;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Logger logger;

    @Inject
    private SyncManager<Long> syncManager;


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transfer(long from, long to, long amount) {
        logger.info("Begin transfer from {} to {} amount {}", from, to, amount);

        syncManager.withLock(from, to, () -> {
            Account debitAccount = getAccountOrThrow(from),
                    creditAccount = getAccountOrThrow(to);

            if (debitAccount.getBalance() >= amount) {
                accountRepository.updateBalance(debitAccount.getId(), -amount);
                accountRepository.updateBalance(creditAccount.getId(), amount);
            } else {
                throw new NotEnoughFundsException(debitAccount.getId());
            }

        });


        logger.info("Successfully transferred from {} to {} amount {}", from, to, amount);
    }

    private Account getAccountOrThrow(long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Account getAccount(long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id));
    }
}




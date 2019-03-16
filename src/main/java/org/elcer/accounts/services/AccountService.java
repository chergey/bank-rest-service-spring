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
import java.math.BigDecimal;

@Service
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Logger logger;

    @Inject
    private Synchronizer<Long> synchronizer;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(long from, long to, BigDecimal amount) {
        logger.info("Begin transfer from {} to {} amount {}", from, to, amount);

        synchronizer.withLock(from, to, () -> {
            Account debitAccount = getAccountOrThrow(from),
                    creditAccount = getAccountOrThrow(to);

            if (debitAccount.getBalance().compareTo(amount) >= 0) {
                 accountRepository.addBalance(debitAccount.getId(), amount.negate());
                 accountRepository.addBalance(creditAccount.getId(), amount);

//                accountRepository.setBalance(debitAccount.getId(), debitAccount.getBalance().subtract(amount));
//                accountRepository.setBalance(creditAccount.getId(), creditAccount.getBalance().add(amount));
            } else {
                throw new NotEnoughFundsException(debitAccount.getId());
            }

        });


        logger.info("Successfully transferred from {} to {} amount {}", from, to, amount);
    }

    private Account getAccountOrThrow(long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account getAccount(long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}




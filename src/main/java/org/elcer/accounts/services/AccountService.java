package org.elcer.accounts.services;


import org.elcer.accounts.exceptions.AccountNotFoundException;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Logger logger;

    @Inject
    private Synchronizer<Long> synchronizer;

    @Inject
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
    }


    public void transfer(long from, long to, BigDecimal amount) {

        logger.info("Begin transfer from {} to {} amount {}", from, to, amount);

        synchronizer.withLock(from, to, () -> {

            transactionTemplate.execute((s) -> {
            Account debitAccount = getAccountOrThrow(from),
                    creditAccount = getAccountOrThrow(to);

                if (debitAccount.getBalance().compareTo(amount) >= 0) {
                    accountRepository.setBalance(debitAccount.getId(), debitAccount.getBalance().subtract(amount));
                    accountRepository.setBalance(creditAccount.getId(), creditAccount.getBalance().add(amount));
                } else {
                    throw new NotEnoughFundsException(debitAccount.getId());
                }
                return null;
            });
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

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public List<Account> getAllAccounts(int page, int size) {
        Page<Account> all = accountRepository.findAll(PageRequest.of(page, size));
        return all.getContent();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account replaceAccount(Long id, Account account) {
        return accountRepository.findById(id).map(oldAccount -> {
            oldAccount.setBalance(account.getBalance());
            oldAccount.setName(account.getName());
            return accountRepository.save(oldAccount);
        }).orElseGet(() -> {
            account.setId(id);
            return accountRepository.save(account);
        });
    }

    public List<Account> getAccounts(String name, int page, int size) {
        Page<Account> allByName = accountRepository.findAllByName(name, PageRequest.of(page, size));
        return allByName.getContent();

    }
}




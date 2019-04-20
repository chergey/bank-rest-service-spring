package org.elcer.accounts.app;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;

@Service
public class SampleDataInitializer {

    private static final int ACCOUNTS_TO_CREATE = 1000;

    @Inject
    private AccountRepository accountRepository;

    private boolean init;

    @PostConstruct
    public void init() {
        if (init) return;
        try {
            for (int i = 1; i < ACCOUNTS_TO_CREATE; i++) {
                var account = new Account(RandomStringUtils.randomAlphabetic(5),
                        BigDecimal.valueOf(RandomUtils.nextLong(100, 10000)));
                accountRepository.save(account);
            }
        } finally {
            init = true;
        }
    }
}

package org.elcer.accounts.db;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;

@Service
@Profile("!test")
public class SampleDataInitializer {

    private static final int ACCOUNTS_TO_CREATE = 1000;

    @Inject
    private AccountRepository accountRepository;

    @PostConstruct
    private void init() {
        for (int i = 1; i < ACCOUNTS_TO_CREATE; i++) {
            var account = new Account(RandomStringUtils.randomAlphabetic(5),
                    BigDecimal.valueOf(RandomUtils.nextLong(100, 10000)));
            accountRepository.save(account);
        }

    }
}

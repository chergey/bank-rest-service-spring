package org.elcer.accounts.app;

import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.elcer.accounts.utils.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;

@Service
public class SampleDataInitializer {

    @Inject
    private AccountRepository accountRepository;

    private boolean init;

    private static final int ACCOUNS_TO_CREATE = 1000;

    @PostConstruct
    public void init() {
        if (init) return;
        try {
            for (int i = 1; i < ACCOUNS_TO_CREATE; i++) {
                Account account = new Account(BigDecimal.valueOf(RandomUtils.getGtZeroRandom()));
                accountRepository.save(account);
            }
        } finally {
            init = true;
        }
    }
}

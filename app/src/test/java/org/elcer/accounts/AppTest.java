package org.elcer.accounts;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.elcer.accounts.services.AccountService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test,eclipselink")
@Slf4j
public class AppTest {



    @Inject
    private AccountService accountService;

    @Inject
    private AccountRepository accountRepository;


    @Repeat(2)
    @Test
    public void testConcurrencyAndDeadlocks() {
        final int times = 14000;

        var first = accountRepository.save(new Account("Mike", BigDecimal.valueOf(26000)));
        var second = accountRepository.save(new Account("Jenny", BigDecimal.valueOf((315000))));
        var third = accountRepository.save(new Account("David", BigDecimal.valueOf((313000))));
        var fourth = accountRepository.save(new Account("Steve", BigDecimal.valueOf(356000)));

        var startingTotal = first.getBalance()
                .add(second.getBalance())
                .add(third.getBalance())
                .add(fourth.getBalance());

        ExecutorUtils.runConcurrentlyFJP(
                () -> transfer(times, first, second),
                () -> transfer(times, second, first),
                () -> transfer(times, third, second),

                () -> transfer(times, second, fourth),
                () -> transfer(times, second, third),
                () -> transfer(times, first, third),
                () -> transfer(times, first, fourth),

                () -> transfer(times, third, first),
                () -> transfer(times, third, fourth),

                () -> transfer(times, fourth, first),
                () -> transfer(times, fourth, second),
                () -> transfer(times, fourth, third)

        );

        var firstInTheEnd = accountService.getAccount(first.getId());
        var secondInTheEnd = accountService.getAccount(second.getId());
        var thirdInTheEnd = accountService.getAccount(third.getId());
        var fourthInTheEnd = accountService.getAccount(fourth.getId());

        var endingTotal = firstInTheEnd.getBalance()
                .add(secondInTheEnd.getBalance())
                .add(thirdInTheEnd.getBalance())
                .add(fourthInTheEnd.getBalance())
                .setScale(0, RoundingMode.UNNECESSARY);

        Assert.assertTrue("Balance can't be less than zero", firstInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", secondInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", thirdInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", fourthInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertEquals(startingTotal, endingTotal);


    }

    private void transfer(final int times, Account debit, Account credit) {
        int i = times;
        while (i-- >= 0) {
            try {
                accountService.transfer(debit.getId(), credit.getId(),
                        BigDecimal.valueOf(RandomUtils.nextLong(100, 10000)));
            } catch (Exception e) {
                if (e instanceof NotEnoughFundsException) {
                    log.info("Not enough money left in {}, stopping", debit.getId());
                    break;
                }
                throw e;
            }
        }
    }


}
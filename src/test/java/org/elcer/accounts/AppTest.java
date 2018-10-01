package org.elcer.accounts;

import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.repo.AccountRepository;
import org.elcer.accounts.services.AccountService;
import org.elcer.accounts.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);


    @Inject
    private AccountService accountService;

    @Inject
    private AccountRepository accountRepository;


    @Test
    public void testConcurrencyAndDeadlocks() {
        final int times = 14000;

        Account first = accountRepository.save(new Account(326000));
        Account second = accountRepository.save(new Account((315000)));
        Account third = accountRepository.save(new Account((313000)));
        Account fourth = accountRepository.save(new Account(356000));

        long startingTotal = second.getBalance() + first.getBalance() + third.getBalance() + fourth.getBalance();

        ExecutorUtils.runConcurrently(
                () -> transfer(times, first, second),
                () -> transfer(times, second, first),
                () -> transfer(times, third, second),

                //  () -> transfer(times, second, fourth),
                () -> transfer(times, second, third),
                () -> transfer(times, first, third),
                //    () -> transfer(times, first, fourth),

                () -> transfer(times, third, second),
                () -> transfer(times, third, first)
                //    () -> transfer(times, third, fourth)

//                () -> transfer(times, fourth, first),
//                () -> transfer(times, fourth, second),
//                () -> transfer(times, fourth, third)

        );

        Account firstInTheEnd = accountRepository.getOne(first.getId());
        Account secondInTheEnd = accountRepository.getOne(second.getId());
        Account thirdInTheEnd = accountRepository.getOne(third.getId());
        Account fourthInTheEnd = accountRepository.getOne(fourth.getId());

        long endingTotal = firstInTheEnd.getBalance() + secondInTheEnd.getBalance() + thirdInTheEnd.getBalance() +
                fourthInTheEnd.getBalance();

        Assert.assertTrue("Balance can't be less than zero", firstInTheEnd.getBalance() >= 0);
        Assert.assertTrue("Balance can't be less than zero", secondInTheEnd.getBalance() >= 0);
        Assert.assertTrue("Balance can't be less than zero", thirdInTheEnd.getBalance() >= 0);
        Assert.assertTrue("Balance can't be less than zero", fourthInTheEnd.getBalance() >= 0);
        Assert.assertEquals(startingTotal, endingTotal);


    }

    private void transfer(final int times, Account debit, Account credit) {
        int i = times;
        while (i-- >= 0) {
            try {
                accountService.transfer(debit.getId(), credit.getId(), RandomUtils.getGtZeroRandom());
            } catch (Exception e) {
                if (e instanceof NotEnoughFundsException) {
                    logger.info("Not enough money left in {}, stopping", debit.getId());
                    break;
                }
                throw e;
            }
        }
    }


}
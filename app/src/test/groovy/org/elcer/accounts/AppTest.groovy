package org.elcer.accounts


import org.apache.commons.lang3.RandomUtils
import org.elcer.accounts.exceptions.NotEnoughFundsException
import org.elcer.accounts.model.Account
import org.elcer.accounts.repo.AccountRepository
import org.elcer.accounts.services.AccountService
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Repeat
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

import javax.inject.Inject
import java.math.RoundingMode

@RunWith(SpringRunner)
@SpringBootTest(classes = App,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test,eclipselink,nosecurity")
class AppTest {

    private static Logger log = LoggerFactory.getLogger(AppTest)

    @Inject
    private AccountService accountService

    @Inject
    private AccountRepository accountRepository


    @Repeat(2)
    @Test
    void testConcurrencyAndDeadlocks() {
        final int times = 14000

        def first = accountRepository.save(new Account("Mike", 26000 as BigDecimal))
        def second = accountRepository.save(new Account("Jenny", 315000 as BigDecimal))
        def third = accountRepository.save(new Account("David", 313000 as BigDecimal))
        def fourth = accountRepository.save(new Account("Steve", 356000 as BigDecimal))

        def startingTotal = first.getBalance()
                .add(second.getBalance())
                .add(third.getBalance())
                .add(fourth.getBalance())

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

        )

        def firstInTheEnd = accountService.getAccount(first.getId())
        def secondInTheEnd = accountService.getAccount(second.getId())
        def thirdInTheEnd = accountService.getAccount(third.getId())
        def fourthInTheEnd = accountService.getAccount(fourth.getId())

        def endingTotal = firstInTheEnd.getBalance()
                .add(secondInTheEnd.getBalance())
                .add(thirdInTheEnd.getBalance())
                .add(fourthInTheEnd.getBalance())
                .setScale(0, RoundingMode.UNNECESSARY)

        Assert.assertTrue("Balance can't be less than zero", firstInTheEnd.getBalance() >= 0)
        Assert.assertTrue("Balance can't be less than zero", secondInTheEnd.getBalance() >= 0)
        Assert.assertTrue("Balance can't be less than zero", thirdInTheEnd.getBalance() >= 0)
        Assert.assertTrue("Balance can't be less than zero", fourthInTheEnd.getBalance() >= 0)
        Assert.assertEquals(startingTotal, endingTotal)


    }

    private void transfer(final int times, Account debit, Account credit) {
        int i = times
        while (i-- >= 0) {
            try {
                accountService.transfer(debit.getId(), credit.getId(),
                        BigDecimal.valueOf(RandomUtils.nextLong(100, 10000)))
            } catch (Exception e) {
                if (e instanceof NotEnoughFundsException) {
                    log.info("Not enough money left in {}, stopping", debit.getId())
                    break
                }
                throw e
            }
        }
    }


}
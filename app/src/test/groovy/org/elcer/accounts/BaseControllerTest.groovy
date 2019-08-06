package org.elcer.accounts

import org.elcer.accounts.model.Account
import org.elcer.accounts.repo.AccountRepository
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

import javax.inject.Inject

@RunWith(SpringRunner)
@SpringBootTest(classes = App, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseControllerTest {

    @Inject
    protected MockMvc mvc

    @MockBean
    protected AccountRepository accountRepository

    protected def preCreateAccount(long id, String name, BigDecimal money) {
        def account = new Account()
                .setId(id)
                .setName(name)
                .setBalance(money)

        Mockito.when(accountRepository.findById(id))
                .thenReturn(Optional.of(account))

        return account
    }
}

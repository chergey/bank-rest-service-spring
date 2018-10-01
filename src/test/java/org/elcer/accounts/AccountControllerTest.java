package org.elcer.accounts;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountResponse;
import org.elcer.accounts.repo.AccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Inject
    private MockMvc mvc;

    @MockBean
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        Account account = new Account();
        account.setId(1L);
        account.setName("Mike Baller");
        account.setBalance(1000);


        Mockito.when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        account = new Account();
        account.setId(2L);
        account.setName("Sam Cally");
        account.setBalance(500);

        Mockito.when(accountRepository.findById(2L))
                .thenReturn(Optional.of(account));
    }


    @Test
    public void testGetByIdSuccessfully() throws Exception {
        mvc.perform(get("/api/account/1"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(response.getCode(), 0);
                    Assert.assertNotNull(response.getAccount());
                    Assert.assertEquals((long) response.getAccount().getId(), 1L);
                });
    }

    @Test
    public void testGetByIdNotFound() throws Exception {
        mvc.perform(get("/api/account/100000"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(response.getCode(), AccountResponse.NO_SUCH_ACCOUNT.getCode());
                    Assert.assertNull(response.getAccount());
                });
    }

    @Test
    public void testNoEnoughFunds() throws Exception {
        mvc.perform(get("/api/account/transfer?from=2&to=1&amount=1000"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(response.getCode(), AccountResponse.NOT_ENOUGH_FUNDS.getCode());
                    Assert.assertNull(response.getAccount());
                });
    }


    private static <T> Object deserialize(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper.readValue(json, objectClass);
    }

}

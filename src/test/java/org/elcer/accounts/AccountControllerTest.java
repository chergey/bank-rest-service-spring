package org.elcer.accounts;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountResponse;
import org.elcer.accounts.repo.AccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
//@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class AccountControllerTest {


    @Inject
    private MockMvc mvc;

    @MockBean
    private AccountRepository accountRepository;

    private List<Account> accountList = new ArrayList<>();

    @Before
    public void setUp() {
        Account account = new Account()
                .setId(1L)
                .setName("Mike Baller")
                .setBalance(BigDecimal.valueOf(1000));

        accountList.add(account);

        Mockito.when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        account = new Account()
                .setId(2L)
                .setName("Sam Cally")
                .setBalance(BigDecimal.valueOf(500));

        accountList.add(account);

        Mockito.when(accountRepository.findById(2L))
                .thenReturn(Optional.of(account));


    }

    @Test
    public void testDeleteAccount() {
        Mockito.doAnswer(invocation -> {
            Mockito.when(accountRepository.findById(1L))
                    .thenReturn(Optional.empty());
            return null;

        }).when(accountRepository).deleteById(1L);
        accountRepository.deleteById(1L);
        Assert.assertFalse(accountRepository.findById(1L).isPresent());
    }

    @Test
    public void testCreateAccount() throws Exception {
        Account createdAcc = new Account(100, "Daniel", BigDecimal.valueOf(1000));

        Mockito.when(accountRepository.save(Mockito.any()))
                .thenReturn(createdAcc);


        Account account = new Account("Daniel", BigDecimal.valueOf(1000));
        mvc.perform(post("/api/accounts/")
                .content(serialize(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(mvcResult ->
                        {
                            String json = mvcResult.getResponse().getContentAsString();
                            Account returnedAccount = (Account) deserialize(json, Account.class);
                            Assert.assertNotNull("No account", returnedAccount);
                            Assert.assertEquals((Long) 100L, returnedAccount.getId());
                        }
                );
    }

    @Test
    public void testGetByAccountIdSuccessfully() throws Exception {
        mvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    Account returnedAccount = (Account) deserialize(json, Account.class);
                    Assert.assertNotNull("No account", returnedAccount);
                    Assert.assertEquals((Long) 1L, returnedAccount.getId());
                });
    }

    @Test
    public void testGetByIdNotFound() throws Exception {
        mvc.perform(get("/api/accounts/100000"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(AccountResponse.noSuchAccount().getCode(), response.getCode());
                });
    }

    @Test
    public void testNoEnoughFunds() throws Exception {
        mvc.perform(get("/api/accounts/transfer?from=2&to=1&amount=1000"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(AccountResponse.notEnoughFunds().getCode(), response.getCode());
                });
    }

    @Test
    public void testAccountTransferSame() throws Exception {
        mvc.perform(get("/api/accounts/transfer?from=2&to=2&amount=1000"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(AccountResponse.debitAccountIsCreditAccount().getCode(), response.getCode());
                });
    }

    @Test
    public void testAccountTransferNegativeAmount() throws Exception {
        mvc.perform(get("/api/accounts/transfer?from=2&to=1&amount=-1000"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                {
                    String json = mvcResult.getResponse().getContentAsString();
                    AccountResponse response = (AccountResponse) deserialize(json, AccountResponse.class);
                    Assert.assertEquals(AccountResponse.negativeAmount().getCode(), response.getCode());
                });
    }

    @Test
    public void testAccountTransfer400() throws Exception {
        mvc.perform(get("/api/accounts/transfer"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllAcounts() throws Exception {
        Mockito.when(accountRepository.findAll((Pageable) Mockito.any()))
                .thenReturn(new PageImpl<>(accountList, Pageable.unpaged(), 2));

        mvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                        {
                            String json = mvcResult.getResponse().getContentAsString();
                            @SuppressWarnings("unchecked") PagedResources<Account> returnedAccount =
                                    (PagedResources<Account>) deserialize(json, new TypeReference<PagedResources<Account>>() {
                                    });
                            Assert.assertNotNull("No response", returnedAccount);
                            Assert.assertNotNull("No account", returnedAccount.getContent());
                            Assert.assertEquals(2, returnedAccount.getContent().size());
                            Assert.assertEquals(1, (long) returnedAccount.getContent().iterator().next().getId());

                        }
                );
    }

    @Test
    public void testGetAccountsByName() throws Exception {
        List<Account> daniels = Lists.newArrayList(
                new Account()
                        .setId(1L)
                        .setName("Daniel")
                        .setBalance(BigDecimal.valueOf(1000)),

                new Account()
                        .setId(2L)
                        .setName("Daniel")
                        .setBalance(BigDecimal.valueOf(1000))
        );

        Mockito.when(accountRepository.findAllByName(Mockito.eq("Daniel"), Mockito.any()))
                .thenReturn(new PageImpl<>(daniels, Pageable.unpaged(), 2));

        mvc.perform(get("/api/accounts/Daniel"))
                .andExpect(status().isOk())
                .andDo(mvcResult ->
                        {
                            String json = mvcResult.getResponse().getContentAsString();
                            @SuppressWarnings("unchecked") PagedResources<Account> returnedAccount =
                                    (PagedResources<Account>) deserialize(json, new TypeReference<PagedResources<Account>>() {
                                    });
                            Assert.assertNotNull("No response", returnedAccount);
                            Assert.assertNotNull("No account", returnedAccount.getContent());
                            Assert.assertEquals(2, returnedAccount.getContent().size());
                            Assert.assertEquals(1, (long) returnedAccount.getContent().iterator().next().getId());

                        }
                );
    }


    private static <T> Object deserialize(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(json, objectClass);
    }

    private static <T> Object deserialize(String json, TypeReference<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(json, objectClass);
    }

    private static String serialize(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.writeValueAsString(object);
    }

}

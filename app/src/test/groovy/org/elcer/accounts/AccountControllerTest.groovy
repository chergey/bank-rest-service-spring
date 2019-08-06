package org.elcer.accounts

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.elcer.accounts.model.Account
import org.elcer.accounts.model.TransferResponse
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.PagedResources
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc(secure = false)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration)
@ActiveProfiles("test,nosecurity")
class AccountControllerTest extends BaseControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper()


    @Test
    void "delete account returns OK"() {
        preCreateAccount(1L, "Mike Adams", 100 as BigDecimal)

        Mockito.doAnswer(invocation -> {
            Mockito.when(accountRepository.findById(1L))
                    .thenReturn(Optional.empty())
        }).when(accountRepository).deleteById(1L)

        mvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    Account returnedAccount = (Account) deserialize(json, Account)
                    Assert.assertNotNull("No account", returnedAccount)
                    Assert.assertEquals((Long) 1L, returnedAccount.getId())
                })


        mvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isOk())

        mvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def response = (TransferResponse) deserialize(json, TransferResponse)
                    Assert.assertEquals(TransferResponse.noSuchAccount().getCode(), response.getCode())
                })

    }

    @Test
    void "create account returns OK"() throws Exception {
        Mockito.when(accountRepository.save(Mockito.any()))
                .thenReturn(new Account(100, "Daniel", 1000 as BigDecimal))

        Account account = new Account("Daniel", 1000 as BigDecimal)
        mvc.perform(post("/api/accounts/")
                .content(serialize(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    Account returnedAccount = (Account) deserialize(json, Account)
                    Assert.assertNotNull("No account", returnedAccount)
                    Assert.assertEquals((Long) 100L, returnedAccount.getId())
                }
                )
    }

    @Test
    void "get account by id returns OK"() throws Exception {
        preCreateAccount(1L, "Mike Adams", 100 as BigDecimal)

        mvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    Account returnedAccount = (Account) deserialize(json, Account)
                    Assert.assertNotNull("No account", returnedAccount)
                    Assert.assertEquals((Long) 1L, returnedAccount.getId())
                })
    }

    @Test
    void "get non-existent account by id return 404"() throws Exception {
        mvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def response = (TransferResponse) deserialize(json, TransferResponse)
                    Assert.assertEquals(TransferResponse.noSuchAccount().getCode(), response.getCode())
                })
    }

    @Test
    void "transfer not enough funds should return CONFLICT "() throws Exception {
        preCreateAccount(1L, "Mike Adams", 1000 as BigDecimal)
        preCreateAccount(2L, "Daniel Rust", 10 as BigDecimal)

        mvc.perform(post("/api/accounts/transfer?from=2&to=1&amount=1000"))
                .andExpect(status().isConflict())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def response = (TransferResponse) deserialize(json, TransferResponse)
                    Assert.assertEquals(TransferResponse.notEnoughFunds().getCode(), response.getCode())
                })
    }

    @Test
    void "transfer to the same account should return 400"() throws Exception {
        mvc.perform(post("/api/accounts/transfer?from=2&to=2&amount=1000"))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def response = (TransferResponse) deserialize(json, TransferResponse)
                    Assert.assertEquals(TransferResponse.debitAccountIsCreditAccount().getCode(), response.getCode())
                })
    }

    @Test
    void "transfer negative amount of money should return 400"() throws Exception {
        mvc.perform(post("/api/accounts/transfer?from=2&to=1&amount=-1000"))
                .andExpect(status().isBadRequest())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def response = (TransferResponse) deserialize(json, TransferResponse)
                    Assert.assertEquals(TransferResponse.negativeAmount().getCode(), response.getCode())
                })
    }

    @Test
    void "transfer with bad uri returns 400"() throws Exception {
        mvc.perform(post("/api/accounts/transfer"))
                .andExpect(status().isBadRequest())
    }

    @Test
    void "get all accounts returns OK"() throws Exception {
        def list = [
                preCreateAccount(1L, "Mike Adams", 1000 as BigDecimal),
                preCreateAccount(2L, "Daniel Rust", 1000 as BigDecimal)
        ]


        Mockito.when(accountRepository.findAll((Pageable) Mockito.any()))
                .thenReturn(new PageImpl<>(list, Pageable.unpaged(), 2))

        mvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def returnedAccount =
                            (PagedResources<Account>) deserialize(json, new TypeReference<PagedResources<Account>>() {
                            })
                    Assert.assertNotNull("No response", returnedAccount)
                    Assert.assertNotNull("No account", returnedAccount.getContent())
                    Assert.assertEquals(2, returnedAccount.getContent().size())
                    Assert.assertEquals(1, (long) returnedAccount.getContent().iterator().next().getId())

                }
                )
    }

    @Test
    void "get accounts by name returns OK"() throws Exception {

        def accounts = [
                new Account()
                        .setId(1L)
                        .setName("Daniel")
                        .setBalance(1000 as BigDecimal),

                new Account()
                        .setId(2L)
                        .setName("Daniel")
                        .setBalance(1000 as BigDecimal)
        ]

        Mockito.when(accountRepository.findAllByName(Mockito.eq("Daniel"), Mockito.any()))
                .thenReturn(new PageImpl<>(accounts, Pageable.unpaged(), 2))

        mvc.perform(get("/api/accounts/Daniel"))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString()
                    def returnedAccount =
                            (PagedResources<Account>) deserialize(json, new TypeReference<PagedResources<Account>>() {
                            })
                    Assert.assertNotNull("No response", returnedAccount)
                    Assert.assertNotNull("No account", returnedAccount.getContent())
                    Assert.assertEquals(2, returnedAccount.getContent().size())
                    Assert.assertEquals(1, (long) returnedAccount.getContent().iterator().next().getId())

                }
                )
    }


    private static <T> Object deserialize(String json, Class<T> objectClass) throws IOException {
        return OBJECT_MAPPER.readValue(json, objectClass)
    }


    private static <T> Object deserialize(String json, TypeReference<T> objectClass) throws IOException {
        return OBJECT_MAPPER.readValue(json, objectClass)
    }

    private static String serialize(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object)
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.USE_ANNOTATIONS, false)
    }

}

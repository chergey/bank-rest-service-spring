package org.elcer.accounts


import org.junit.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@AutoConfigureMockMvc
class AccountSecurityTest extends BaseControllerTest {


    @Test
    void "should return Forbidden"() throws Exception {
        preCreateAccount(1L, "Mike Adams", 100 as BigDecimal)
        mvc.perform(post("/api/accounts/transfer?from=2&to=1&amount=1000"))
                .andExpect(status().isUnauthorized())
    }

    @WithMockUser(username = "admin")
    @Test
    void "transfer authorized return OK"() throws Exception {
        preCreateAccount(1L, "Mike Adams", 100 as BigDecimal)
        preCreateAccount(2L, "Daniel Rust", 100 as BigDecimal)

        mvc.perform(post("/api/accounts/transfer?from=2&to=1&amount=10"))
                .andExpect(status().isOk())
    }


}

package org.elcer.accounts.controller;


import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountResponse;
import org.elcer.accounts.services.AccountService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;



@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Inject
    private AccountService accountService;



    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable("id") long id) {
        Account account = accountService.getAccount(id);

        AccountResponse accountResponse = new AccountResponse("", 0);
        accountResponse.setAccount(account);
        return accountResponse;
    }

    @GetMapping("/transfer")
    public AccountResponse transfer(@RequestParam("from") long from, @RequestParam("to") long to,
                                    @RequestParam("amount") BigDecimal amount) {

        if (from == to) {
            return AccountResponse.debitAccountIsCreditAccount();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return AccountResponse.negativeAmount();
        }
        accountService.transfer(from, to, amount);
        return AccountResponse.success();
    }

}

package org.elcer.accounts.controller;


import org.elcer.accounts.exceptions.AccountNotFoundException;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountResponse;
import org.elcer.accounts.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;


@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Inject
    private AccountService accountService;

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<AccountResponse> handleNoAccount(AccountNotFoundException exception) {
        return new ResponseEntity<AccountResponse>(AccountResponse.NO_SUCH_ACCOUNT.addAccountId(exception.getAccountId()),
                HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(NotEnoughFundsException.class)
    public AccountResponse handleNotEnoughFunds(NotEnoughFundsException exception) {
        return AccountResponse.NOT_ENOUGH_FUNDS.addAccountId(exception.getAccountId());
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable("id") long id) {
        Account account = accountService.getAccount(id);

        AccountResponse accountResponse = new AccountResponse("", 0);
        accountResponse.setAccount(account);
        return accountResponse;
    }

    @GetMapping("/transfer")
    public AccountResponse transfer(@RequestParam("from") long from, @RequestParam("to") long to,
                                    @RequestParam("amount") int amount) {

        if (from == to) {
            return AccountResponse.DEBIT_ACCOUNT_IS_CREDIT_ACCOUNT;
        }
        if (amount < 0) {
            return AccountResponse.NEGATIVE_AMOUNT;
        }
        accountService.transfer(from, to, amount);
        return AccountResponse.SUCCESS;
    }

}

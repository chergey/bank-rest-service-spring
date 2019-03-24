package org.elcer.accounts.controller;


import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountListResponse;
import org.elcer.accounts.model.AccountResponse;
import org.elcer.accounts.services.AccountService;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigDecimal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@Validated
@RequestMapping("/api")
public class AccountController {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountResourceAssembler accountResourceAssembler;

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Resource<Account>> createAccount(@Valid @RequestBody Account account) {
        var savedAccount = accountService.createAccount(account);

        return ResponseEntity
                .created(linkTo(methodOn(AccountController.class).createAccount(account)).toUri())
                .body(accountResourceAssembler.toResource(savedAccount));

    }

    @PutMapping("/accounts/id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Account> createAccount(@Valid @RequestBody Account account, @PathVariable long id) {
        return accountResourceAssembler.toResource(accountService.replaceAccount(id, account));
    }


    /**
     * Get account by id
     *
     * @param id
     * @return account
     */
    @GetMapping("/accounts/{id:\\d+}")
    public Resource<Account> getAccount(@PathVariable long id) {
        Account account = accountService.getAccount(id);
        return accountResourceAssembler.toResource(account);
    }


    @GetMapping("/accounts/{name:[a-zA-Z]+}")
    public AccountListResponse getAccountByName(@PathVariable String name,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        var accounts = accountService.getAccounts(name, page, size);
        return new AccountListResponse()
                .setAccounts(accounts)
                .setNoMore(accounts.size() < size);

    }


    @GetMapping("/accounts")
    public AccountListResponse getAllAccounts(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        var accounts = accountService.getAllAccounts(page, size);
        return new AccountListResponse()
                .setAccounts(accounts)
                .setNoMore(accounts.size() < size);
    }

    @GetMapping("/accounts/transfer")
    public AccountResponse transfer(@RequestParam long from, @RequestParam long to,
                                    @RequestParam BigDecimal amount) {

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

package org.elcer.accounts.controller;


import org.elcer.accounts.model.Account;
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
import java.util.List;

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
        Account account1 = accountService.createAccount(account);

        return ResponseEntity
                .created(linkTo(methodOn(AccountController.class).createAccount(account)).toUri())
                .body(accountResourceAssembler.toResource(account1));

    }

    @PutMapping("/accounts/id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Account> createAccount(@Valid @RequestBody Account account, @PathVariable Long id) {
        return accountResourceAssembler.toResource(accountService.replaceAccount(id, account));
    }


    /**
     * Get account by id
     * @param id
     * @return account
     */
    @GetMapping("/accounts/{id}")
    public Resource<Account> getAccount(@PathVariable long id) {
        Account account = accountService.getAccount(id);
        return accountResourceAssembler.toResource(account);
    }


    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
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

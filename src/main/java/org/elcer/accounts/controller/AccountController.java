package org.elcer.accounts.controller;


import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.TransferResponse;
import org.elcer.accounts.services.AccountService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountResourceAssembler accountResourceAssembler;

    @Inject
    private PagedResourcesAssembler<Account> pagedResourcesAssembler;

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
        Account createdAccount = accountService.replaceAccount(id, account);
        return accountResourceAssembler.toResource(createdAccount);
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
    public PagedResources<Resource<Account>> getAccountsByName(@PathVariable String name,
                                                               @PageableDefault Pageable pageable) {
        var accounts = accountService.getAccounts(name, pageable);
        return pagedResourcesAssembler.toResource(accounts);

    }


    @GetMapping("/accounts")
    public PagedResources<Resource<Account>>  getAllAccounts(@PageableDefault Pageable pageable) {
        var accounts = accountService.getAllAccounts(pageable);
        return pagedResourcesAssembler.toResource(accounts);
    }

    @PostMapping("/accounts/transfer")
    public TransferResponse transfer(@RequestParam long from, @RequestParam long to,
                                     @RequestParam BigDecimal amount) {

        if (from == to) {
            return TransferResponse.debitAccountIsCreditAccount();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return TransferResponse.negativeAmount();
        }
        accountService.transfer(from, to, amount);
        return TransferResponse.success();
    }

}

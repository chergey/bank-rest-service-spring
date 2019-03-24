package org.elcer.accounts.controller;

import org.elcer.accounts.model.Account;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class AccountResourceAssembler implements ResourceAssembler<Account, Resource<Account>> {

    @Override
    public Resource<Account> toResource(Account account) {
        return new Resource<>(account,
                linkTo(methodOn(AccountController.class).getAccount(account.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).getAllAccounts(0, 20)).withRel("accounts"));
    }
}
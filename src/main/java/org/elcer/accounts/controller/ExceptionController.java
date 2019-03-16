package org.elcer.accounts.controller;

import org.elcer.accounts.exceptions.AccountNotFoundException;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.AccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<AccountResponse> handleNoAccount(AccountNotFoundException exception) {
        return new ResponseEntity<>(AccountResponse.noSuchAccount()
                .withAccountId(exception.getAccountId()),
                HttpStatus.NOT_FOUND);
    }


    @ResponseBody
    @ExceptionHandler(NotEnoughFundsException.class)
    public AccountResponse handleNotEnoughFunds(NotEnoughFundsException exception) {
        return AccountResponse.notEnoughFunds().withAccountId(exception.getAccountId());
    }
}

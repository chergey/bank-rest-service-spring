package org.elcer.accounts.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {


    //api responses
    public static AccountResponse NEGATIVE_AMOUNT = new AccountResponse("Amount to transfer must be positive", 1);
    public static AccountResponse SUCCESS = new AccountResponse("Successfully transferred funds", 0);
    public static AccountResponse ERROR_UPDATING = new AccountResponse("Error while updating funds", 2);
    public static AccountResponse NO_SUCH_ACCOUNT = new AccountResponse("No account with id %d", 6);
    public static AccountResponse NOT_ENOUGH_FUNDS = new AccountResponse("Not enough funds on account id %d", 3);
    public static AccountResponse DEBIT_ACCOUNT_IS_CREDIT_ACCOUNT = new AccountResponse("Debit account can't be credit account", 4);


    private String message;
    private int code;
    private Account account;

    private static final String SPACE = " ";


    public AccountResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }


    public AccountResponse appendMessage(String sep, String data) {
        AccountResponse response = new AccountResponse(this.message, this.code);
        response.message += sep + SPACE + data;
        return response;
    }


    public AccountResponse addAccountId(long accountId) {
        AccountResponse response = new AccountResponse(this.message, this.code);
        response.message = String.format(this.message, accountId);
        return response;
    }
}

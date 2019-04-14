package org.elcer.accounts.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponse {


    //api responses
    public static TransferResponse negativeAmount() {
        return new TransferResponse("Amount to transfer must be positive", 1);
    }

    public static TransferResponse success() {
        return new TransferResponse("Successfully transferred funds", 0);
    }

    public static TransferResponse errorUpdating() {
        return new TransferResponse("Error while updating funds", 2);
    }

    public static TransferResponse noSuchAccount() {
        return new TransferResponse("No account with id %d", 6);
    }

    public static TransferResponse notEnoughFunds() {
        return new TransferResponse("Not enough funds on account id %d", 3);
    }

    public static TransferResponse debitAccountIsCreditAccount() {
        return new TransferResponse("Debit account can't be credit account", 4);
    }


    private String message;
    private int code;

    private static final String SPACE = " ";


    private TransferResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }


    public TransferResponse appendMessage(String sep, String data) {
        TransferResponse response = new TransferResponse(this.message, this.code);
        response.message += sep + SPACE + data;
        return response;
    }


    public TransferResponse withAccountId(long accountId) {
        TransferResponse response = new TransferResponse(this.message, this.code);
        response.message = String.format(this.message, accountId);
        return response;
    }
}

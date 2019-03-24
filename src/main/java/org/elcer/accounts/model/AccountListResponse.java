package org.elcer.accounts.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@Accessors(chain = true)
public class AccountListResponse {
    private List<Account> accounts;
    private boolean noMore;
}

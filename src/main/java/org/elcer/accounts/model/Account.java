package org.elcer.accounts.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Accessors(chain = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // ApacheDB don't work well with IDENTITY (https://issues.apache.org/jira/browse/DERBY-5151)
    private Long id;

    private String name;
    private BigDecimal balance;

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public void subtractBalance(BigDecimal balance) {
        this.balance = this.balance.subtract(balance);
    }

    public void increaseBalance(BigDecimal balance) {
        this.balance = this.balance.add(balance);
    }


}
package org.elcer.accounts.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // ApacheDB don't work well with IDENTITY (https://issues.apache.org/jira/browse/DERBY-5151)
    private Long id;

    private String name;
    private long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(long balance) {
        this.balance = balance;
    }

    public void subtractBalance(long balance) {
        this.balance -= balance;
    }

    public void increaseBalance(long balance) {
        this.balance += balance;
    }


}
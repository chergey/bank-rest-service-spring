package org.elcer.accounts.repo;


import org.elcer.accounts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("update Account a set a.balance = a.balance + ?2 where a.id = ?1")
    void updateBalance(long id, long balance);


}

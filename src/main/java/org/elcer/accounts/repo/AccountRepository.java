package org.elcer.accounts.repo;


import org.elcer.accounts.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Repository
@Transactional(propagation = Propagation.MANDATORY)

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query("update Account a set a.balance = a.balance + ?2 where a.id = ?1")
    void addBalance(long id, BigDecimal balance);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Account a set a.balance = ?2 where a.id = ?1")
    void setBalance(long id, BigDecimal balance);

    Page<Account> findAllByName(String name, Pageable pageable);


}

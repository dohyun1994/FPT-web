package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    Optional<Account> findOneByEmailIgnoreCase(String email);

    Optional<Account> findByToken(String token);

    @Query("SELECT DISTINCT a FROM Account a JOIN a.schedules s")
    List<Account> findAllWithSchedules();

    @Query("SELECT a FROM Account a WHERE a.name LIKE %:name% AND EXISTS (" +
            "SELECT s FROM Schedule s WHERE s.account.id = a.id)")
    List<Account> findAccountsWithScheduleByNameContaining(String name);

}
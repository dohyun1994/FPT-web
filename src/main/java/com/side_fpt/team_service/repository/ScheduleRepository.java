package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
//    Optional<Schedule> findByAccountAndDate(Account account, LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.account = :account AND s.date = :date")
    Optional<Schedule> findByAccountAndDate(@Param("account") Account account, @Param("date") LocalDate date);


    List<Schedule> findByAccount(Account account);

    Optional<Schedule> findByAccountIdAndDate(Long accountId, LocalDate date);
}

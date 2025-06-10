package com.side_fpt.team_service.service;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Schedule;
import com.side_fpt.team_service.repository.AccountRepository;
import com.side_fpt.team_service.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;

    public void saveOrUpdateSchedule(String email, String title, LocalDate date) {
        Account account = getAccountByEmail(email);

        Optional<Schedule> existingSchedule = scheduleRepository.findByAccountAndDate(account, date);

        if (existingSchedule.isPresent()) {
            Schedule schedule = existingSchedule.get();
            schedule.setTitle(title);
            schedule.setModifiedAt(LocalDateTime.now());
            scheduleRepository.save(schedule);
        } else {
            Schedule schedule = new Schedule();
            schedule.setAccount(account);
            schedule.setTitle(title);
            schedule.setDate(date);
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setModifiedAt(LocalDateTime.now());
            scheduleRepository.save(schedule);
        }
    }


    public void updateScheduleTitle(Long scheduleId, String newTitle, String userEmail) {
        Schedule schedule = getScheduleById(scheduleId);
        validateScheduleOwner(schedule, userEmail);

        schedule.setTitle(newTitle);
        schedule.setModifiedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }


    public void deleteSchedule(Long scheduleId, String userEmail) {
        Schedule schedule = getScheduleById(scheduleId);
        validateScheduleOwner(schedule, userEmail);

        scheduleRepository.delete(schedule);

    }



    public List<Account> getUsersWithSchedule() {
        return scheduleRepository.findAll()
                .stream()
                .map(Schedule::getAccount)
                .distinct()
                .collect(Collectors.toList());
    }




    private Account getAccountByEmail(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다: " + email));
    }

    private Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다: ID " + id));
    }

    private void validateScheduleOwner(Schedule schedule, String userEmail) {
        if (!schedule.getAccount().getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("해당 일정에 대한 권한이 없습니다.");
        }
    }
}
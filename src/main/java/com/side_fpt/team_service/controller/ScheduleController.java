package com.side_fpt.team_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side_fpt.team_service.dto.schedule.ScheduleDTO;
import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Schedule;
import com.side_fpt.team_service.repository.AccountRepository;
import com.side_fpt.team_service.repository.ScheduleRepository;
import com.side_fpt.team_service.service.AccountService;
import com.side_fpt.team_service.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/calendar")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/calendar")
    public String showCalendarPage(Model model) {

        List<Account> usersWithSchedule = accountService.getUsersWithSchedules();
        model.addAttribute("usersWithSchedule", usersWithSchedule);

        return "/calendar/calendar";
    }


    @GetMapping("/searchUsers")
    @ResponseBody
    public List<Map<String, Object>> searchUsersAjax(@RequestParam String keyword) {
        List<Account> users = accountService.searchAccountsWithSchedule(keyword);

        return users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            return map;
        }).collect(Collectors.toList());
    }



    @GetMapping("/addSchedule")
    public String addScheduleForm() {
        return "/calendar/addSchedule";
    }


    @PostMapping("/save")
    public String saveSchedule(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String title, @RequestParam String date) {
        scheduleService.saveOrUpdateSchedule(userDetails.getUsername(), title, LocalDate.parse(date));
        return "redirect:/calendar/calendar";
    }


    @GetMapping("/userSchedule/{userId}")
    public String showUserSchedule(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails, Model model) {

        Account user = accountRepository.findById(userId).orElseThrow();
        List<Schedule> schedules = scheduleRepository.findByAccount(user);

        boolean isOwner = userDetails.getUsername().equals(user.getEmail());

        List<ScheduleDTO> scheduleDtos = schedules.stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("schedules", scheduleDtos);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("username", userDetails.getUsername());

        return "/calendar/userSchedule";

    }



    @GetMapping("/getSchedule")
    @ResponseBody
    public ResponseEntity<?> getSchedule(@RequestParam Long userId, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        Optional<Schedule> schedule = scheduleRepository.findByAccountIdAndDate(userId, localDate);

        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().body(null));
    }


    @PostMapping("/update")
    public String updateSchedule(@RequestParam Long id, @RequestParam String title, @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.updateScheduleTitle(id, title, userDetails.getUsername());
        return "redirect:/calendar/calendar";
    }

    @PostMapping("/delete")
    public String deleteSchedule(@RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.deleteSchedule(id, userDetails.getUsername());
        return "redirect:/calendar/calendar";
    }

}
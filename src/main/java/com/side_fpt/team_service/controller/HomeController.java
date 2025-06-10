package com.side_fpt.team_service.controller;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails){
        if (userDetails != null) {
            Optional<Account> accountOpt = accountRepository.findOneByEmailIgnoreCase(userDetails.getUsername());
            accountOpt.ifPresent(account -> model.addAttribute("account", account));
        }
        return "home";
    }
}

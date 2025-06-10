package com.side_fpt.team_service.controller;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.service.AccountService;
import com.side_fpt.team_service.service.EmailService;
import com.side_fpt.team_service.util.email.EmailDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;


    @Autowired
    private EmailService emailService;


    @Value("${site.domain}")
    private String site_domain;

    @Value("${password.token.reset.timeout.minutes}")
    private int password_token_timeout;



    @GetMapping("/login")
    public String loginPage() {

        return "/user/login";
    }

    @GetMapping("/forgot-password")
    public String forgot_password(Model model) {

        return "/user/forgot_password";
    }



    @PostMapping("/reset-password")
    public String reset_password(@RequestParam("email") String email, RedirectAttributes attributes, Model model) {
        Optional<Account> optional_account = accountService.findOneByEmail(email);
        if (optional_account.isPresent()) {
            Account account = accountService.findById(optional_account.get().getId()).get();
            String reset_token = UUID.randomUUID().toString();
            account.setToken(reset_token);
            account.setPassword_reset_token_expiry(LocalDateTime.now().plusMinutes(password_token_timeout));
            accountService.save(account);

            String reset_message = "This is the reset password link: "+site_domain+"change-password?token="+reset_token;
            EmailDetails emailDetails = new EmailDetails(account.getEmail(), reset_message, "Reset password");
            if(emailService.sendSimpleEmail(emailDetails) == false){
                attributes.addFlashAttribute("error", "Error while sending email, contact admin");
                return "redirect:/forgot-password";
            }
            attributes.addFlashAttribute("message", "Password reset email sent");
            return "redirect:/login";

        } else {
            attributes.addFlashAttribute("error", "No user found with the email supplied");
            return "redirect:/forgot-password";

        }

    }


    @GetMapping("/change-password")
    public String change_password(Model model, @RequestParam("token") String token, RedirectAttributes attributes){
        if (token.equals("")){
            attributes.addFlashAttribute("error", "Invalid Token");
            return "redirect:/forgot-password";
        }
        Optional<Account> optional_account = accountService.findByToken(token);
        if(optional_account.isPresent()){
            Account account = accountService.findById(optional_account.get().getId()).get();
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(optional_account.get().getPassword_reset_token_expiry())){
                attributes.addFlashAttribute("error", "Token Expired");
                return "redirect:/forgot-password";
            }
            model.addAttribute("account", account);
            return "user/change_password";
        }

        attributes.addFlashAttribute("error", "Invalid token");
        return "redirect:/forgot-password";
    }



    @PostMapping("/change-password")
    public String post_change_password(@ModelAttribute Account account,  RedirectAttributes attributes) {
        Account account_by_id = accountService.findById(account.getId()).get();
        account_by_id.setPassword(account.getPassword());
        account_by_id.setToken("");
        accountService.save(account_by_id);
        attributes.addFlashAttribute("message", "Password updated");
        return  "redirect:/login";

    }



    @GetMapping("/register")
    public String registerPage(Model model) {
        Account account = new Account();
        model.addAttribute("account", account);

        return "/user/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute Account account, BindingResult result,@RequestParam("password2") String password2, @RequestParam("imageFile") MultipartFile imageFile) {

        if(!account.getEmail().toLowerCase().endsWith("@fpt.com")) {
            result.rejectValue("email", "email.invalid", "Only @fpt.com emails are allowed");
        }

        if(password2 != null) {

            if (!account.getPassword().equals(password2)) {
                result.rejectValue("password", "password.mismatch", "Passwords do not match");
            }

        }

        if(result.hasErrors()) {
            return "/user/register";
        }

        accountService.save(account, imageFile);
        return "redirect:/";

    }


    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailDuplicate(@RequestParam("email") String email) {

        return accountService.isEmailDuplicate(email);
    }



    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, Principal principal) {
        String authUser = "email";
        if (principal != null) {
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
//            model.addAttribute("photo", account.getPhoto());
            return "/user/profile";
        } else {
            return "redirect:/?error";
        }

    }


    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute Account account, BindingResult result, @RequestParam("password2") String password2, @RequestParam("imageFile") MultipartFile imageFile,  Model model) {

        if (!account.getPassword().equals(password2)) {
            result.rejectValue("password", "password.mismatch", "Passwords do not match");
        }

        if(result.hasErrors()) {
            return "/user/profile";
        }

        accountService.save(account, imageFile);
        return "redirect:/";

    }

}

//package com.side_fpt.team_service.config;
//
//import com.side_fpt.team_service.model.Account;
//import com.side_fpt.team_service.model.Authority;
//import com.side_fpt.team_service.service.AccountService;
//import com.side_fpt.team_service.service.AuthorityService;
//import com.side_fpt.team_service.util.constant.Privillages;
//import com.side_fpt.team_service.util.constant.Roles;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SeedData implements CommandLineRunner{
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private AuthorityService authorityService;
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        for (Privillages auth : Privillages.values()) {
//            Authority authority = new Authority();
//            authority.setId(auth.getId());
//            authority.setName(auth.getPrivillage());
//            authorityService.save(authority);
//
//        }
//
//        Account account01 = new Account();
//
//        account01.setEmail("admin@fpt.com");
//        account01.setPassword("1234");
//        account01.setName("adminUser");
//        account01.setRole(Roles.ADMIN.getRole());

//        account04.setEmail("super_editor@editor.com");
//        account04.setPassword("1234");
//        account04.setName("Editor");
//        account04.setRole(Roles.EDITOR.getRole());
//
//        Set<Authority> authorities = new HashSet<>();
//        authorityService.findById(Privillages.ACCESS_ADMIN_PANEL.getId()).ifPresent(authorities::add);
//        authorityService.findById(Privillages.RESET_ANY_USER_PASSWORD.getId()).ifPresent(authorities::add);
//        account04.setAuthorities(authorities);
//
//        accountService.save(account01);
//
//    }
//}
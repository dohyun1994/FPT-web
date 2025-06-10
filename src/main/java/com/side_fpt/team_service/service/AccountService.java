package com.side_fpt.team_service.service;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Authority;
import com.side_fpt.team_service.repository.AccountRepository;
import com.side_fpt.team_service.util.constant.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getRole() == null) {
            account.setRole(Roles.USER.getRole());
        }

        return accountRepository.save(account);
    }


    public Account save(Account account, MultipartFile imageFile){

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (account.getRole() == null){
            account.setRole(Roles.USER.getRole());
        }

        // 이미지 파일 저장 처리
        if (imageFile != null && !imageFile.isEmpty()) {

            try {
                // 저장 경로 (예: /uploads 폴더, 실서버 환경에 맞게 조정 가능)
                String uploadDir = "D:/uploads";
                Files.createDirectories(Paths.get(uploadDir));

                // 고유 파일 이름 생성
                String originalFilename = imageFile.getOriginalFilename();
                String newFilename = UUID.randomUUID() + "_" + originalFilename;

                // 실제 파일 저장
                File destination = new File(uploadDir + "/" + newFilename);
                imageFile.transferTo(destination);

                // DB에 저장될 파일 이름 세팅
                account.setProfile_image(newFilename);

            } catch (IOException e) {
                e.printStackTrace();
                // 필요한 경우 예외 처리 로직 추가 가능
            }
        }


        return accountRepository.save(account);
    }



    public boolean isEmailDuplicate(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email).isPresent();
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findOneByEmailIgnoreCase(email);
        if(!optionalAccount.isPresent()){
            throw new UsernameNotFoundException("Account not found");
        }
        Account account = optionalAccount.get();

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(account.getRole()));


        for(Authority _auth: account.getAuthorities()){
            grantedAuthority.add(new SimpleGrantedAuthority(_auth.getName()));
        }

        return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }


    public Optional<Account> findOneByEmail(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email);
    }


    public Optional<Account> findById(long id) {

        return accountRepository.findById(id);
    }


    public Optional<Account> findByToken(String token) {
        return accountRepository.findByToken(token);
    }

    public List<Account> getUsersWithSchedules() {
        return accountRepository.findAllWithSchedules();
    }


    public List<Account> searchAccountsWithSchedule(String name) {
        return accountRepository.findAccountsWithScheduleByNameContaining(name);
    }


    public Account getByEmail(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));
    }

}

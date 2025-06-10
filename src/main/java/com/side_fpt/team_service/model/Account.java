package com.side_fpt.team_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Email(message = "Invaild Email")
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email missing")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "Password missing")
    private String password;

    private String name;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;


    private String profile_image;


    private String role;

    @Column(name="token")
    private String token;

    private LocalDateTime password_reset_token_expiry;

    @ManyToMany
    @JoinTable(
            name="account_authority",
            joinColumns = {@JoinColumn(name="account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private Set<Authority> authorities = new HashSet<>();



    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Schedule> schedules = new HashSet<>();
    
}

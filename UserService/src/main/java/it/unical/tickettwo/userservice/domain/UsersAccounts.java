package it.unical.tickettwo.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_accounts")
public class UsersAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name="username", unique = true, nullable = false)
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="role", nullable = false)
    private String role;
    @Column(name="access_type", nullable = false)
    private String accessType;
}

package it.unical.tickettwo.userservice.controller;

import it.unical.tickettwo.userservice.util.JwtUtil;
import it.unical.tickettwo.userservice.domain.UsersAccounts;
import it.unical.tickettwo.userservice.dto.UsersAccountsDTO;
import it.unical.tickettwo.userservice.service.UsersAccountsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@CrossOrigin(value = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {
    private class AuthToken {
        String token;
        UsersAccounts user;

        public UsersAccounts getUser() {
            return user;
        }

        public void setUser(UsersAccounts user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @Autowired
    private UsersAccountsService usersAccountsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthToken login(@RequestBody UsersAccounts user) {
        String username = user.getUsername();
        String password = user.getPassword();

        UsersAccounts storedUser = usersAccountsService.getUserByUsername(username);

        if (storedUser != null && passwordEncoder.matches(password, storedUser.getPassword())) {
            String token = JwtUtil.generateToken(username);
            AuthToken auth = new AuthToken();
            auth.setToken(token);
            auth.setUser(storedUser);
            return auth;
        }

        return null;
    }


    @PostMapping("/logout")
    public boolean logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            System.out.println("user Logging out");
            session.invalidate();
            return true;
        }
        return false;
    }

    @PostMapping("/isAuthenticated")
    public boolean isAuthenticated(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return JwtUtil.validateToken(token);
        }

        return false;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<UsersAccountsDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (JwtUtil.validateToken(token)) {
                String username = JwtUtil.extractUsername(token);
                UsersAccounts user = usersAccountsService.getUserByUsername(username);
                if (user != null) {
                    System.out.println("Token: " + token);
                    System.out.println("Username estratto: " + username);
                    System.out.println("Utente trovato: " + user);

                    UsersAccountsDTO dto = new UsersAccountsDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getRole(),
                            user.getAccessType()
                    );
                    return ResponseEntity.ok(dto);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}

package it.unical.tickettwo.userservice.controller;

import it.unical.tickettwo.userservice.JwtUtil;
import it.unical.tickettwo.userservice.domain.UsersAccounts;
import it.unical.tickettwo.userservice.dto.UsersAccountsDTO;
import it.unical.tickettwo.userservice.service.UsersAccountsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/me")
    public ResponseEntity<UsersAccountsDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (JwtUtil.validateToken(token)) {
                String username = JwtUtil.extractUsername(token);
                UsersAccounts user = usersAccountsService.getUserByUsername(username);
                if (user != null) {
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


    public UsersAccounts getUserByToken(String token) {
        if (token != null) {
            String decode = decodeBase64(token);
            String[] parts = decode.split(":");
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];

                System.out.println("Username: " + username);
                System.out.println("Password: " + password);

                UsersAccounts user = usersAccountsService.getUserByUsername(username);
                if (user != null) {
                    System.out.println("Stored password: " + user.getPassword());
                    System.out.println("Password match: " + passwordEncoder.matches(password, user.getPassword()));

                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    private static String codeBase64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    private static String decodeBase64(String value) {
        return new String(Base64.getDecoder().decode(value));
    }
}

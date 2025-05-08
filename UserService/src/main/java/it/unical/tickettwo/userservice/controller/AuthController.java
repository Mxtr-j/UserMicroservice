package it.unical.tickettwo.userservice.controller;

import it.unical.tickettwo.userservice.domain.UsersAccounts;
import it.unical.tickettwo.userservice.service.UsersAccountsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AuthToken login(@RequestBody UsersAccounts user, HttpServletRequest req) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();
        System.out.println(username);
        System.out.println(password);
        System.out.print("password: " + password);
        UsersAccounts storedUser = usersAccountsService.getUserByUsername(username);
        System.out.println("storedUsername = " + storedUser.getUsername());
        System.out.println("storedPassword = " + storedUser.getPassword());

        System.out.println(passwordEncoder.matches(password, storedUser.getPassword()));
        if (storedUser != null && passwordEncoder.matches(password, storedUser.getPassword())) {
            String concat = username + ":" + password;
            String token = codeBase64(concat);
            HttpSession session = req.getSession();
            session.setAttribute("user", storedUser);
            AuthToken auth = new AuthToken();
            auth.setToken(token);
            auth.setUser(storedUser);
            System.out.println("Token:" + token);
            return auth;
        }
        System.out.println("loginError");
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
        String auth = req.getHeader("Authorization");
        System.out.println("Authorization Header: " + auth);
        if (auth != null && auth.startsWith("Basic ")) {
            String token = auth.substring("Basic ".length());
            System.out.println("Token: " + token);
            UsersAccounts user = getUserByToken(token);
            System.out.println("Authenticated User: " + user);
            return user != null;
        }
        System.out.println("Authorization Header is missing or invalid");
        return false;
    }


    public UsersAccounts getUserByToken(String token) {
        if (token != null) {
            String decode = decodeBase64(token);
            String[] parts = decode.split(":");
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];
                UsersAccounts user = usersAccountsService.getUserByUsername(username);
                if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                    return user;
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

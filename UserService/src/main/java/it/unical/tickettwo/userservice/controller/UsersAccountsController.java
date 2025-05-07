package it.unical.tickettwo.userservice.controller;

import it.unical.tickettwo.userservice.domain.UsersAccounts;
import it.unical.tickettwo.userservice.service.UsersAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersAccountsController {

    @Autowired
    private UsersAccountsService usersAccountsService;

    @GetMapping
    public List<UsersAccounts> getAllUsers() {
        return usersAccountsService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersAccounts> getUserById(@PathVariable Long id) {
        Optional<UsersAccounts> user = usersAccountsService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsersAccounts createUser(@RequestBody UsersAccounts user) {
        return usersAccountsService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersAccountsService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsersAccounts> getUserByUsername(@PathVariable String username) {
        UsersAccounts user = usersAccountsService.getUserByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}

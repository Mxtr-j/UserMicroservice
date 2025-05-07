package it.unical.tickettwo.userservice.service;

import it.unical.tickettwo.userservice.domain.UsersAccounts;
import it.unical.tickettwo.userservice.repository.UsersAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersAccountsService {

    @Autowired
    private UsersAccountsRepository usersAccountsRepository;

    public List<UsersAccounts> getAllUsers() {
        return usersAccountsRepository.findAll();
    }

    public Optional<UsersAccounts> getUserById(Long id) {
        return usersAccountsRepository.findById(id);
    }

    public UsersAccounts createUser(UsersAccounts user) {
        return usersAccountsRepository.save(user);
    }

    public void deleteUser(Long id) {
        usersAccountsRepository.deleteById(id);
    }

    public UsersAccounts getUserByUsername(String username) {
        return usersAccountsRepository.findByUsername(username);
    }
}

package com.example.hadapp.service;

import com.example.hadapp.model.User;
import com.example.hadapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password){
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(username, password);
        return userOptional.orElse(null);
    }

    public Optional<User> getUserById(long l) {
        return userRepository.findById((int) l);
    }
}

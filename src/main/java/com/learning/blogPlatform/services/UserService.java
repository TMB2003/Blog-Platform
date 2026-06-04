package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.enums.Role;
import com.learning.blogPlatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(String name, User newUser){
        User oldUser = userRepository.findByUserName(name);

        if(oldUser == null) return null;

        if(newUser.getEmail() != null) oldUser.setEmail(newUser.getEmail());
        if(newUser.getPassword() != null) oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if(newUser.getRole() != null) oldUser.setRole(newUser.getRole());

        return saveUser(oldUser);
    }

    public void deleteUser(String name){
        userRepository.deleteByUserName(name);
    }
}

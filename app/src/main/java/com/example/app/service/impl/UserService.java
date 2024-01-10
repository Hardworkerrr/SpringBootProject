package com.example.app.service.impl;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.DefaultEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService implements DefaultEntityService<User> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void add(User user) {
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);
    }

    @Override
    public void addAll(List<User> e) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }

}

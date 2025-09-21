package com.example.DevPlayground.config;

import com.example.DevPlayground.entity.Role;
import com.example.DevPlayground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    
    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void run(String... args) {
        if (userService.getAllUsers().isEmpty()) {
            userService.createUser("user", "user@example.com", "password", Role.USER);
            userService.createUser("admin", "admin@example.com", "password", Role.ADMIN);
        }
    }
}
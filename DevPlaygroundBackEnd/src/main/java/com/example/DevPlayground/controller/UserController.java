package com.example.DevPlayground.controller;

import com.example.DevPlayground.entity.User;
import com.example.DevPlayground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService UserService) {
        this.userService = UserService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/by-username")
    public User getUserByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username).orElse(null);
    }

    @GetMapping("/by-email")
    public User getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email).orElse(null);
    }

    @GetMapping("/exists-by-username")
    public boolean userExistsByUsername(@RequestParam String username) {
        return userService.userExistsByUsername(username);
    }

    @GetMapping("/exists-by-email")
    public boolean userExistsByEmail(@RequestParam String email) {
        return userService.userExistsByEmail(email);
    }

    @PostMapping("/save")
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }
}

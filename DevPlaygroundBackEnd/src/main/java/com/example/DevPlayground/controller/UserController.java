package com.example.DevPlayground.controller;

import com.example.DevPlayground.entity.Role;
import com.example.DevPlayground.entity.Users;
import com.example.DevPlayground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.isEnabled()
                ))
                .toList();
        return ResponseEntity.ok(userResponses);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        System.out.println("Create user request received: " + request);
        System.out.println("Current authentication: " + 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
        
        try {
            // Check if username already exists
            if (userService.existsByUsername(request.username())) {
                throw new RuntimeException("Username already exists");
            }
            
            Role role = Role.valueOf(request.role().toUpperCase());
            Users createdUser = userService.createUser(
                    request.username(),
                    request.email(),
                    request.password(),
                    role
            );
            
            UserResponse response = new UserResponse(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail(),
                    createdUser.getRole().name(),
                    createdUser.isEnabled()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    public record CreateUserRequest(String username, String email, String password, String role) {
    }

    public record UserResponse(Long id, String username, String email, String role, boolean enabled) {
    }
}

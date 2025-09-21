package com.example.DevPlayground.service;

import com.example.DevPlayground.entity.User;
import com.example.DevPlayground.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by username
     *
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     *
     * @param email the email of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Check if a user exists by username
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if a user exists by email
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Save a user
     *
     * @param user the user to save
     * @return the saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete all users
     */
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}

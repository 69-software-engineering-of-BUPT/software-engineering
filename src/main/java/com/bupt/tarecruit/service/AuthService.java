package com.bupt.tarecruit.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this(Paths.get("."));
    }

    public AuthService(Path root) {
        this.userRepository = new UserRepository(root);
    }

    public AuthenticatedUser authenticate(String userId, String password) throws Exception {
        if (isBlank(userId) || isBlank(password)) {
            throw new AuthenticationException("User ID and password are required.");
        }
        User user = userRepository.getUserById(userId.trim());
        if (user == null) {
            throw new AuthenticationException("Unknown user ID.");
        }
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Incorrect password.");
        }
        if (!isSupportedRole(user.getRole())) {
            throw new AuthenticationException("Unsupported user role.");
        }
        return new AuthenticatedUser(user.getUserId(), user.getRole(), user.getName());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isSupportedRole(String role) {
        return "TA".equals(role) || "MO".equals(role) || "ADMIN".equals(role);
    }
}

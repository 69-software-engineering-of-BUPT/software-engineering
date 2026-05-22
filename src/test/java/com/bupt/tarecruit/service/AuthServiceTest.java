package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

public class AuthServiceTest {
    private final AuthService authService = new AuthService();
    private final UserRepository userRepository = new UserRepository();

    @Test
    public void authenticateReturnsActiveUserDetails() throws Exception {
        String userId = "AUTH_ACTIVE_" + System.nanoTime();
        saveTempUser(userId, "password123", "TA", "Active TA", "ACTIVE");

        try {
            AuthenticatedUser user = authService.authenticate(userId, "password123");

            assertEquals(userId, user.getUserId());
            assertEquals("TA", user.getRole());
            assertEquals("Active TA", user.getName());
        } finally {
            userRepository.deleteUser(userId);
        }
    }

    @Test
    public void authenticateRejectsFrozenUsers() throws Exception {
        String userId = "AUTH_FROZEN_" + System.nanoTime();
        saveTempUser(userId, "password123", "TA", "Frozen TA", "FROZEN");

        try {
            expectAuthenticationFailure(userId, "password123",
                    "This account has been frozen by the administrator.");
        } finally {
            userRepository.deleteUser(userId);
        }
    }

    @Test
    public void authenticateRejectsUnsupportedRoles() throws Exception {
        String userId = "AUTH_ROLE_" + System.nanoTime();
        saveTempUser(userId, "password123", "GUEST", "Guest User", "ACTIVE");

        try {
            expectAuthenticationFailure(userId, "password123", "Unsupported user role.");
        } finally {
            userRepository.deleteUser(userId);
        }
    }

    private void saveTempUser(String userId, String password, String role, String name, String status) throws Exception {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        user.setRole(role);
        user.setName(name);
        user.setStatus(status);
        userRepository.saveUser(user);
    }

    private void expectAuthenticationFailure(String userId, String password, String message) throws Exception {
        try {
            authService.authenticate(userId, password);
        } catch (AuthenticationException ex) {
            assertEquals(message, ex.getMessage());
            return;
        }
        throw new AssertionError("Expected AuthenticationException");
    }
}

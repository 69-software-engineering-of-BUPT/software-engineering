package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

/**
 * Encapsulates authentication rules for the login flow.
 * The service loads persisted users, validates credentials, and returns the
 * subset of account data that should be stored in session.
 */
public class AuthService {
    private final UserRepository userRepository;

    /**
     * Creates a service backed by the default user repository.
     */
    public AuthService() {
        this(new UserRepository());
    }

    /**
     * Creates a service with an injected repository.
     *
     * @param userRepository repository used to fetch account records
     */
    AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates credentials against the stored user record.
     *
     * @param userId submitted account identifier
     * @param password submitted plain-text password
     * @return authenticated session payload for the matching account
     * @throws AuthenticationException when validation fails or the account cannot log in
     * @throws Exception when repository access fails unexpectedly
     */
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
        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            throw new AuthenticationException("This account has been frozen by the administrator.");
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

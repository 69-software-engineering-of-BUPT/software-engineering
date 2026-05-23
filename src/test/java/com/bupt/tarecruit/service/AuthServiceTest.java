package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

public class AuthServiceTest {
    @Test
    public void authenticateRejectsBlankCredentials() throws Exception {
        AuthService authService = new AuthService(new StubUserRepository());

        assertAuthenticationError("User ID and password are required.",
                () -> authService.authenticate("   ", ""));
    }

    @Test
    public void authenticateRejectsUnknownUserId() throws Exception {
        AuthService authService = new AuthService(new StubUserRepository());

        assertAuthenticationError("Unknown user ID.",
                () -> authService.authenticate("TA404", "secret"));
    }

    @Test
    public void authenticateRejectsIncorrectPassword() throws Exception {
        StubUserRepository userRepository = new StubUserRepository()
                .withUser(user("TA001", "correct-password", "TA", "ACTIVE", "Alice"));
        AuthService authService = new AuthService(userRepository);

        assertAuthenticationError("Incorrect password.",
                () -> authService.authenticate("TA001", "wrong-password"));
    }

    @Test
    public void authenticateRejectsFrozenAccount() throws Exception {
        StubUserRepository userRepository = new StubUserRepository()
                .withUser(user("TA001", "secret", "TA", "FROZEN", "Alice"));
        AuthService authService = new AuthService(userRepository);

        assertAuthenticationError("This account has been frozen by the administrator.",
                () -> authService.authenticate("TA001", "secret"));
    }

    @Test
    public void authenticateRejectsUnsupportedRole() throws Exception {
        StubUserRepository userRepository = new StubUserRepository()
                .withUser(user("USR001", "secret", "GUEST", "ACTIVE", "Visitor"));
        AuthService authService = new AuthService(userRepository);

        assertAuthenticationError("Unsupported user role.",
                () -> authService.authenticate("USR001", "secret"));
    }

    @Test
    public void authenticateReturnsAuthenticatedUserForValidCredentials() throws Exception {
        StubUserRepository userRepository = new StubUserRepository()
                .withUser(user("TA001", "secret", "TA", "ACTIVE", "Alice"));
        AuthService authService = new AuthService(userRepository);

        AuthenticatedUser authenticatedUser = authService.authenticate("  TA001  ", "secret");

        assertEquals("TA001", userRepository.lastRequestedUserId);
        assertNotNull(authenticatedUser);
        assertEquals("TA001", authenticatedUser.getUserId());
        assertEquals("TA", authenticatedUser.getRole());
        assertEquals("Alice", authenticatedUser.getName());
    }

    private void assertAuthenticationError(String expectedMessage, ThrowingAction action) throws Exception {
        try {
            action.run();
        } catch (AuthenticationException ex) {
            assertEquals(expectedMessage, ex.getMessage());
            return;
        }
        throw new AssertionError("Expected AuthenticationException with message: " + expectedMessage);
    }

    private User user(String userId, String password, String role, String status, String name) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        user.setName(name);
        return user;
    }

    private interface ThrowingAction {
        void run() throws Exception;
    }

    private static final class StubUserRepository extends UserRepository {
        private User user;
        private String lastRequestedUserId;

        private StubUserRepository withUser(User user) {
            this.user = user;
            return this;
        }

        @Override
        public User getUserById(String userId) throws IOException {
            lastRequestedUserId = userId;
            return user;
        }
    }
}

package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class AuthServiceTest {
    private final AuthService authService = new AuthService();

    @Test
    public void authenticateWithValidTaCredentialsReturnsAuthenticatedUser() throws Exception {
        AuthenticatedUser user = authService.authenticate("TA001", "password123");

        assertNotNull(user);
        assertEquals("TA001", user.getUserId());
        assertEquals("TA", user.getRole());
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateWithWrongPasswordThrowsAuthenticationException() throws Exception {
        authService.authenticate("TA001", "wrongpassword");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateWithBlankUserIdThrowsAuthenticationException() throws Exception {
        authService.authenticate("", "password123");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateWithBlankPasswordThrowsAuthenticationException() throws Exception {
        authService.authenticate("TA001", "");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateWithUnknownUserIdThrowsAuthenticationException() throws Exception {
        authService.authenticate("UNKNOWN999", "password123");
    }
}

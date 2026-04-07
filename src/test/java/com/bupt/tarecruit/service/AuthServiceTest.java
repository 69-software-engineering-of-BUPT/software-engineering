package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private final AuthService service = new AuthService();

    @Test
    void authenticatesExistingTaUser() throws Exception {
        AuthenticatedUser user = service.authenticate("TA001", "password123");

        assertEquals("TA001", user.getUserId());
        assertEquals("TA", user.getRole());
        assertEquals("Alice Wang", user.getName());
    }

    @Test
    void rejectsUnknownUser() {
        assertThrows(AuthenticationException.class, () -> service.authenticate("UNKNOWN", "password123"));
    }

    @Test
    void rejectsWrongPassword() {
        assertThrows(AuthenticationException.class, () -> service.authenticate("TA001", "wrong"));
    }
}

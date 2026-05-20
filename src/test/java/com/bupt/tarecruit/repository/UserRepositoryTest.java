package com.bupt.tarecruit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.bupt.tarecruit.model.User;

public class UserRepositoryTest {
    private final UserRepository userRepository = new UserRepository();

    @Test
    public void getUserByIdReadsAdminAccountFromJsonFile() throws IOException {
        User admin = userRepository.getUserById("ADMIN001");

        assertNotNull(admin);
        assertEquals("ADMIN001", admin.getUserId());
        assertEquals("ADMIN", admin.getRole());
    }

    @Test
    public void getAllUsersLoadsSeedAccountsForAdminMonitoring() throws IOException {
        List<User> users = userRepository.getAllUsers();

        assertFalse(users.isEmpty());
    }

    @Test
    public void getUserByIdReadsTaWorkloadField() throws IOException {
        User ta = userRepository.getUserById("TA001");

        assertNotNull(ta);
        assertEquals("TA", ta.getRole());
        assertEquals(1, ta.getActiveJobsCount());
    }
}

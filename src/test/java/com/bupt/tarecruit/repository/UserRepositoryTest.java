package com.bupt.tarecruit.repository;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.bupt.tarecruit.model.User;

// TA001 (profile save) + TA004 (CV path storage): UserRepository write tests

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
        User user = new User();
        user.setUserId("TA_TEST_WORKLOAD");
        user.setRole("TA");
        user.setActiveJobsCount(2);

        try {
            userRepository.saveUser(user);

            User ta = userRepository.getUserById("TA_TEST_WORKLOAD");
            assertNotNull(ta);
            assertEquals("TA", ta.getRole());
            assertEquals(2, ta.getActiveJobsCount());
        } finally {
            userRepository.deleteUser("TA_TEST_WORKLOAD");
        }
    }

    // ------------------------------------------------------------------ //
    // TA001 AC3 + TA004 AC3: profile fields and CV path written to JSON  //
    // ------------------------------------------------------------------ //

    @Test
    public void saveUserPersistsAllProfileFieldsIncludingCvPath() throws IOException {
        User user = new User();
        user.setUserId("TA_TEST_SAVE");
        user.setRole("TA");
        user.setName("Test Student");
        user.setEmail("test@bupt.edu.cn");
        user.setPhoneNumber("13800138000");
        user.setResearchArea("Machine Learning");
        user.setCet6Grade("580");
        user.setCvFilePath("uploads/cv_TA_TEST_SAVE.pdf");

        try {
            userRepository.saveUser(user);

            User loaded = userRepository.getUserById("TA_TEST_SAVE");
            assertNotNull(loaded);
            assertEquals("Test Student",              loaded.getName());
            assertEquals("test@bupt.edu.cn",          loaded.getEmail());
            assertEquals("13800138000",               loaded.getPhoneNumber());
            assertEquals("Machine Learning",          loaded.getResearchArea());
            assertEquals("580",                       loaded.getCet6Grade());
            assertEquals("uploads/cv_TA_TEST_SAVE.pdf", loaded.getCvFilePath());
        } finally {
            userRepository.deleteUser("TA_TEST_SAVE");
        }
    }
}

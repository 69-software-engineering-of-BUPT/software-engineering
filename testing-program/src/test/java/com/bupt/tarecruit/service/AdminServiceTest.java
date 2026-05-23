package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;

public class AdminServiceTest {
    private final AdminService adminService = new AdminService();

    @Test
    public void isTaAtUpperLimitReturnsTrueWhenTaHasThreeActiveJobs() {
        User ta = user("TA001", "TA", 3);

        assertTrue(adminService.isTaAtUpperLimit(ta));
    }

    @Test
    public void isTaAtUpperLimitReturnsFalseForMoAndAvailableTa() {
        User availableTa = user("TA002", "TA", 2);
        User mo = user("MO001", "MO", 3);

        assertFalse(adminService.isTaAtUpperLimit(availableTa));
        assertFalse(adminService.isTaAtUpperLimit(mo));
    }

    @Test
    public void filterUsersByRoleReturnsOnlyMatchingRole() {
        List<User> users = Arrays.asList(
                user("TA001", "TA", 1),
                user("MO001", "MO", 0),
                user("ADMIN001", "ADMIN", 0));

        List<User> taUsers = adminService.filterUsersByRole(users, "TA");

        assertEquals(1, taUsers.size());
        assertEquals("TA001", taUsers.get(0).getUserId());
    }

    @Test
    public void filterOpenJobsReturnsOnlyOpenJobs() {
        Job openJob = job("JOB001", "OPEN");
        Job closedJob = job("JOB002", "CLOSED");

        List<Job> openJobs = adminService.filterOpenJobs(Arrays.asList(openJob, closedJob));

        assertEquals(1, openJobs.size());
        assertEquals("JOB001", openJobs.get(0).getJobId());
    }

    @Test
    public void isJobActionNeededWhenOpenJobHasNoApprovedApplication() {
        Job openJob = job("JOB001", "OPEN");
        Application pending = application("APP001", "PENDING");

        assertTrue(adminService.isJobActionNeeded(openJob, Collections.singletonList(pending)));
    }

    @Test
    public void isJobActionNeededReturnsFalseWhenJobAlreadyHasApprovedApplication() {
        Job openJob = job("JOB001", "OPEN");
        Application approved = application("APP001", "APPROVED");

        assertFalse(adminService.isJobActionNeeded(openJob, Collections.singletonList(approved)));
    }

    private User user(String userId, String role, int activeJobsCount) {
        User user = new User();
        user.setUserId(userId);
        user.setRole(role);
        user.setActiveJobsCount(activeJobsCount);
        return user;
    }

    private Job job(String jobId, String status) {
        Job job = new Job();
        job.setJobId(jobId);
        job.setStatus(status);
        return job;
    }

    private Application application(String applicationId, String status) {
        Application application = new Application();
        application.setApplicationId(applicationId);
        application.setStatus(status);
        return application;
    }
}

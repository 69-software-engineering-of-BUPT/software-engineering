package com.bupt.tarecruit.service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;

public class AdminService {
    private static final int TA_ACTIVE_JOB_LIMIT = 3;

    public boolean isTaAtUpperLimit(User user) {
        return user != null
                && "TA".equalsIgnoreCase(user.getRole())
                && user.getActiveJobsCount() >= TA_ACTIVE_JOB_LIMIT;
    }

    public List<User> filterUsersByRole(List<User> users, String role) {
        if (users == null || role == null || "all".equalsIgnoreCase(role)) {
            return users == null ? Collections.emptyList() : users;
        }

        String expectedRole = role.toUpperCase(Locale.ROOT);
        return users.stream()
                .filter(user -> user != null && expectedRole.equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<Job> filterOpenJobs(List<Job> jobs) {
        if (jobs == null) {
            return Collections.emptyList();
        }

        return jobs.stream()
                .filter(job -> job != null && "OPEN".equalsIgnoreCase(job.getStatus()))
                .collect(Collectors.toList());
    }

    public long countApprovedApplications(List<Application> applications) {
        if (applications == null) {
            return 0;
        }

        return applications.stream()
                .filter(application -> application != null
                        && "APPROVED".equalsIgnoreCase(application.getStatus()))
                .count();
    }

    public boolean isJobActionNeeded(Job job, List<Application> applications) {
        return job != null
                && "OPEN".equalsIgnoreCase(job.getStatus())
                && countApprovedApplications(applications) == 0;
    }
}

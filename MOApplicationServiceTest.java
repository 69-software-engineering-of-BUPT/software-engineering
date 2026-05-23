package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.repository.UserRepository;

public class MOApplicationServiceTest {
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final UserRepository userRepository = new UserRepository();

    private String appId;
    private String jobId;
    private String taId;

    @Before
    public void setUp() {
        appId = "TEST_APP_" + UUID.randomUUID();
        jobId = "TEST_JOB_" + UUID.randomUUID();
        taId = "TA_TEST_" + UUID.randomUUID();
    }

    @After
    public void tearDown() {
        deleteFile("data/applications/" + appId + "_application.json");
        deleteFile("data/jobs/JOB_" + jobId + ".json");
        deleteFile("data/users/USER_" + taId + ".json");
    }

    @Test
    public void updateApplicationStatusApprovingApplicationStoresApprovedStatusAndMarksMo() throws Exception {
        String moId = "MO_TEST_APPROVE";

        Job job = new Job();
        job.setJobId(jobId);
        job.setMoId(moId);
        job.setMdId(moId);
        job.setModuleName("Test Module");
        job.setLeaderCount(1);
        job.setMemberCount(1);
        job.setStatus("OPEN");
        jobRepository.save(job);

        User taUser = new User();
        taUser.setUserId(taId);
        taUser.setRole("TA");
        taUser.setStatus("ACTIVE");
        taUser.setActiveJobsCount(0);
        userRepository.saveUser(taUser);

        Application application = new Application();
        application.setApplicationId(appId);
        application.setJobId(jobId);
        application.setStudentId(taId);
        application.setStatus("PENDING");
        application.setStatement("Test application for MO approval.");
        application.setApplicationType("L");
        application.setCvAttached(false);
        applicationRepository.save(application);

        Application updated = applicationService.updateApplicationStatus(appId, "APPROVED", "Strong candidate", moId, "L");

        assertEquals("APPROVED", updated.getStatus());
        assertEquals("Strong candidate", updated.getFeedback());
        assertEquals(moId, updated.getMarkedBy());
        assertTrue(updated.getMarkTime() != null && !updated.getMarkTime().isEmpty());

        Job persistedJob = jobRepository.findById(jobId);
        assertEquals(0, persistedJob.getLeaderCount());
        assertEquals("OPEN", persistedJob.getStatus());

        User updatedTa = userRepository.getUserById(taId);
        // PENDING and APPROVED both count against the TA quota, so no quota delta should happen here.
        assertEquals(0, updatedTa.getActiveJobsCount());
    }

    @Test
    public void appendMoMessageAppendsFormattedMoChatEntryToApplicationStatement() throws Exception {
        String moId = "MO_TEST_REPLY";

        Job job = new Job();
        job.setJobId(jobId);
        job.setMoId(moId);
        job.setMdId(moId);
        job.setModuleName("Conversation Test Module");
        job.setLeaderCount(1);
        job.setMemberCount(1);
        job.setStatus("OPEN");
        jobRepository.save(job);

        Application application = new Application();
        application.setApplicationId(appId);
        application.setJobId(jobId);
        application.setStudentId(taId);
        application.setStatus("PENDING");
        application.setStatement("Initial application statement.");
        application.setApplicationType("NL");
        application.setCvAttached(false);
        applicationRepository.save(application);

        applicationService.appendMOMessage(appId, moId, "Please upload a CV.");

        Application persisted = applicationRepository.findById(appId);
        String statement = persisted.getStatement();

        assertTrue(statement.startsWith("Initial application statement."));
        assertTrue(statement.contains("Please upload a CV."));
        assertTrue(statement.contains("MO]:"));
    }

    private void deleteFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }
}

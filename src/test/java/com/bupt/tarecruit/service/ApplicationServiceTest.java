package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;

class ApplicationServiceTest {

    @Test
    void rejectsDuplicateApplicationForSameTaAndJob() throws Exception {
        Path root = TestDataWorkspace.copyProjectData();
        ApplicationService service = new ApplicationService(root);

        Application application = new Application();
        application.setStudentId("TA001");
        application.setJobId("JOB001");
        application.setStatement("Interested");

        assertThrows(ApplicationException.class, () -> service.submitApplication(application));
    }

    @Test
    void acceptsApplicationForAnotherOpenJob() throws Exception {
        Path root = TestDataWorkspace.copyProjectData();
        JobService jobService = new JobService(root);
        Job extraJob = new Job();
        extraJob.setJobId("JOBT002");
        extraJob.setMoId("MO001");
        extraJob.setMdName("Dr. Smith");
        extraJob.setModuleName("Algorithms");
        extraJob.setJobType("Teaching Assistant");
        extraJob.setRequirements("Java");
        extraJob.setWeeklyWorkload(8);
        extraJob.setDeadline("2099-12-31");
        extraJob.setStatus("OPEN");
        jobService.createJob(extraJob);

        long beforeCount = jsonCount(root.resolve("data").resolve("applications"));

        ApplicationService service = new ApplicationService(root);
        Application application = new Application();
        application.setStudentId("TA001");
        application.setJobId("JOBT002");
        application.setStatement("Interested");

        service.submitApplication(application);

        assertEquals(beforeCount + 1, jsonCount(root.resolve("data").resolve("applications")));
        assertTrue(service.getTAApplicationList("TA001").stream().anyMatch(item -> "JOBT002".equals(item.getJobId())));
    }

    @Test
    void rejectsClosedJobApplication() throws Exception {
        Path root = TestDataWorkspace.copyProjectData();
        JobService jobService = new JobService(root);
        Job extraJob = new Job();
        extraJob.setJobId("JOBT003");
        extraJob.setMoId("MO001");
        extraJob.setMdName("Dr. Smith");
        extraJob.setModuleName("Closed Module");
        extraJob.setJobType("Teaching Assistant");
        extraJob.setRequirements("Java");
        extraJob.setWeeklyWorkload(8);
        extraJob.setDeadline("2099-12-31");
        extraJob.setStatus("CLOSED");
        jobService.createJob(extraJob);

        ApplicationService service = new ApplicationService(root);
        Application application = new Application();
        application.setStudentId("TA001");
        application.setJobId("JOBT003");
        application.setStatement("Interested");

        assertThrows(ApplicationException.class, () -> service.submitApplication(application));
    }

    @Test
    void reviewUpdatesApplicationAndCreatesNotification() throws Exception {
        Path root = TestDataWorkspace.copyProjectData();
        JobService jobService = new JobService(root);
        Job extraJob = new Job();
        extraJob.setJobId("JOBT004");
        extraJob.setMoId("MO001");
        extraJob.setMdName("Dr. Smith");
        extraJob.setModuleName("Databases");
        extraJob.setJobType("Teaching Assistant");
        extraJob.setRequirements("SQL");
        extraJob.setWeeklyWorkload(8);
        extraJob.setDeadline("2099-12-31");
        extraJob.setStatus("OPEN");
        jobService.createJob(extraJob);

        ApplicationService service = new ApplicationService(root);
        Application application = new Application();
        application.setStudentId("TA001");
        application.setJobId("JOBT004");
        application.setStatement("Interested");
        service.submitApplication(application);

        String applicationId = service.getTAApplicationList("TA001").stream()
            .filter(item -> "JOBT004".equals(item.getJobId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("application not found"))
            .getApplicationId();

        long beforeCount = jsonCount(root.resolve("data").resolve("notifications"));
        service.reviewApplication(applicationId, "MO001", "APPROVED", "Welcome");

        assertEquals(beforeCount + 1, jsonCount(root.resolve("data").resolve("notifications")));
    }

    private long jsonCount(Path directory) throws Exception {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream.filter(path -> path.getFileName().toString().endsWith(".json")).count();
        }
    }
}

package com.bupt.tarecruit.service;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.bupt.tarecruit.model.Job;

public class JobServiceTest {
    private final JobService jobService = new JobService();

    @Test
    public void getOpenJobsReturnsNonEmptyList() throws Exception {
        List<Job> openJobs = jobService.getOpenJobs();

        assertFalse(openJobs.isEmpty());
    }

    @Test
    public void getOpenJobsContainsOnlyOpenStatusJobs() throws Exception {
        List<Job> openJobs = jobService.getOpenJobs();

        for (Job job : openJobs) {
            assertEquals("OPEN", job.getStatus().toUpperCase());
        }
    }

    @Test
    public void getJobByIdReturnsCorrectJobForJOB001() throws Exception {
        Job job = jobService.getJobById("JOB001");

        assertNotNull(job);
        assertEquals("JOB001", job.getJobId());
        assertEquals("MO001", job.getMdId());
        assertEquals("OPEN", job.getStatus());
    }

    // ------------------------------------------------------------------ //
    // TA003 AC2: clear display of course name, MO name, requirements,    //
    // introduction — all fields TA needs to browse and choose a position  //
    // ------------------------------------------------------------------ //

    @Test
    public void getJobByIdReturnsDisplayFieldsForTA003() throws Exception {
        Job job = jobService.getJobById("JOB001");

        assertNotNull(job);
        // course name (moduleName)
        assertEquals("EBU6304 Software Engineering", job.getModuleName());
        // MO display name
        assertEquals("Dr. Smith", job.getMdName());
        // position requirements
        assertNotNull(job.getRequirements());
        // position introduction
        assertNotNull(job.getIntroduction());
    }

    @Test
    public void getOpenJobsAreSortedByPublishedAtDescending() throws Exception {
        List<Job> openJobs = jobService.getOpenJobs();

        for (int i = 0; i < openJobs.size() - 1; i++) {
            String current = openJobs.get(i).getPublishedAt();
            String next = openJobs.get(i + 1).getPublishedAt();
            if (current != null && next != null) {
                assertFalse(
                    "Jobs should be sorted newest first",
                    current.compareTo(next) < 0
                );
            }
        }
    }
}

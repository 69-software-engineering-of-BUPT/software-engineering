package com.bupt.tarecruit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.bupt.tarecruit.model.Job;

public class JobRepositoryTest {
    private final JobRepository jobRepository = new JobRepository();

    @Test
    public void getJobByIdReadsPublishedJobForAdminProjectMonitoring() throws IOException {
        Job job = jobRepository.getJobById("JOB001");

        assertNotNull(job);
        assertEquals("JOB001", job.getJobId());
        assertEquals("MO001", job.getMdId());
        assertEquals("OPEN", job.getStatus());
    }

    @Test
    public void getAllJobsLoadsSeedJobs() throws Exception {
        List<Job> jobs = jobRepository.getAllJobs();

        assertFalse(jobs.isEmpty());
    }
}

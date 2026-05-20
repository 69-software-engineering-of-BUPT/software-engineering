package com.bupt.tarecruit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.bupt.tarecruit.model.Application;

public class ApplicationRepositoryTest {
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    @Test
    public void getApplicationsByJobIdLoadsApplicationsForVacancyMonitoring() throws IOException {
        List<Application> applications = applicationRepository.getApplicationsByJobId("JOB001");

        assertFalse(applications.isEmpty());
        assertEquals("JOB001", applications.get(0).getJobId());
    }

    @Test
    public void getApplicationsByTaIdLoadsApplicationsForTaWorkloadReview() throws IOException {
        List<Application> applications = applicationRepository.getApplicationsByTaId("TA001");

        assertFalse(applications.isEmpty());
        assertEquals("TA001", applications.get(0).getTaId());
    }
}

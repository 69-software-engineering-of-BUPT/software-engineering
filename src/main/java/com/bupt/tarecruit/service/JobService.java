package com.bupt.tarecruit.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.JobRepository;

public class JobService {
    private final JobRepository jobRepo = new JobRepository();

    /**
     * Return jobs that are currently open for TA browsing.
     */
    public List<Job> getOpenJobs() throws Exception {
        List<Job> all = jobRepo.getAllJobs();
        List<Job> open = new ArrayList<>();

        for (Job job : all) {
            if (!"OPEN".equalsIgnoreCase(job.getStatus())) continue;
            open.add(job);
        }
        open.sort(Comparator.comparing(Job::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return open;
    }

    public Job getJobById(String jobId) throws Exception {
        return jobRepo.findById(jobId);
    }
}

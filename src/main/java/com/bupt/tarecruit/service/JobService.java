package com.bupt.tarecruit.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.JobRepository;

public class JobService {
    private final JobRepository jobRepo = new JobRepository();

    /**
     * Return only OPEN jobs whose deadline has not passed.
     */
    public List<Job> getOpenJobs() throws Exception {
        List<Job> all = jobRepo.getAllJobs();
        List<Job> open = new ArrayList<>();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        for (Job job : all) {
            if (!"OPEN".equalsIgnoreCase(job.getStatus())) continue;
            if (job.getDeadline() != null && job.getDeadline().compareTo(today) < 0) continue;
            open.add(job);
        }
        open.sort(Comparator.comparing(Job::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return open;
    }

    public Job getJobById(String jobId) throws Exception {
        return jobRepo.findById(jobId);
    }
}

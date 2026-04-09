package com.bupt.tarecruit.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.util.IdGenerator;
import com.bupt.tarecruit.util.TimeUtil;

public class JobService {
    private final JobRepository jobRepository;

    public JobService() {
        this(Paths.get("."));
    }

    public JobService(Path root) {
        this.jobRepository = new JobRepository(root);
    }

    public void createJob(Job job) throws Exception {
        if (job.getJobId() == null || job.getJobId().trim().isEmpty()) {
            job.setJobId(IdGenerator.newId("JOB"));
        }
        if (job.getStatus() == null || job.getStatus().trim().isEmpty()) {
            job.setStatus("OPEN");
        }
        if (job.getPublishedAt() == null || job.getPublishedAt().trim().isEmpty()) {
            job.setPublishedAt(TimeUtil.nowDateTime());
        }
        jobRepository.save(job);
    }

    public Job findById(String jobId) throws Exception {
        return jobRepository.findById(jobId);
    }

    public Job getJobById(String jobId) throws Exception {
        return findById(jobId);
    }

    public List<Job> getOpenJobs() throws Exception {
        List<Job> result = new ArrayList<Job>();
        for (Job job : jobRepository.getAllJobs()) {
            if ("OPEN".equalsIgnoreCase(job.getStatus()) && !TimeUtil.isExpired(job.getDeadline())) {
                result.add(job);
            }
        }
        result.sort(Comparator.comparing(Job::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public List<Job> getJobsByMoId(String moId) throws Exception {
        return jobRepository.findByMoId(moId);
    }
}

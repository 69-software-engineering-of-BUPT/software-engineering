package com.bupt.tarecruit.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.JsonUtil;

public class JobRepository {
    // Data is stored in data/jobs/ directory
    private static final String DATA_PATH = "data/jobs/";
    private static final String DATA_DIR = "data/jobs/";

    public void saveJob(Job job) throws IOException {
        String filePath = DATA_DIR + "JOB_" + job.getJobId() + ".json";
        JsonUtil.saveToJsonFile(job, filePath);
    }

    public Job getJobById(String jobId) throws IOException {
        String filePath = DATA_DIR + "JOB_" + jobId + ".json";
        return JsonUtil.readFromJsonFile(filePath, Job.class);
    }
    /**
     * Save or update a job post.
     * File naming convention: JOB_{jobId}.json
     */
    public void save(Job job) throws Exception {
        String filePath = DATA_PATH + "JOB_" + job.getJobId() + ".json";
        
        File dir = new File(DATA_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Standardized to writeToJsonFile to match JsonUtil implementation
        JsonUtil.saveToJsonFile(job, filePath);
    }

    /**
     * Find a job by its ID.
     * Renamed from getJobById to findById to match ApplicationService logic.
     */
    public Job findById(String jobId) throws Exception {
        String filePath = DATA_PATH + "JOB_" + jobId + ".json";
        File file = new File(filePath);
        
        if (!file.exists()) {
            return null;
        }
        
        return JsonUtil.readFromJsonFile(filePath, Job.class);
    }

    /**
     * Retrieve all jobs stored in the data directory
     */
    public List<Job> getAllJobs() throws Exception {
        List<Job> jobs = new ArrayList<>();
        File dir = new File(DATA_PATH);
        
        if (dir.exists() && dir.isDirectory()) {
            // Filter all JSON files in the jobs directory
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    Job job = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Job.class);
                    if (job != null) {
                        jobs.add(job);
                    }
                }
            }
        }
        return jobs;
    }
}
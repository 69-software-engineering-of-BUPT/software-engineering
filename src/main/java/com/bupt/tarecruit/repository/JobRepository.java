package com.bupt.tarecruit.repository;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobRepository {
    private static final String DATA_DIR = "data/jobs/";

    public void saveJob(Job job) throws IOException {
        String filePath = DATA_DIR + "JOB_" + job.getJobId() + ".json";
        JsonUtil.saveToJsonFile(job, filePath);
    }

    public Job getJobById(String jobId) throws IOException {
        String filePath = DATA_DIR + "JOB_" + jobId + ".json";
        return JsonUtil.readFromJsonFile(filePath, Job.class);
    }

    public List<Job> getAllJobs() throws IOException {
        List<Job> jobs = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
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
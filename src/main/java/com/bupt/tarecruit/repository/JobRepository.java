package com.bupt.tarecruit.repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.JsonUtil;

public class JobRepository {
    private final Path dataDir;

    public JobRepository() {
        this(Paths.get("."));
    }

    public JobRepository(Path root) {
        this.dataDir = root.resolve("data").resolve("jobs");
    }

    public void save(Job job) throws Exception {
        JsonUtil.saveToJsonFile(job, dataDir.resolve("JOB_" + job.getJobId() + ".json").toString());
    }

    public Job findById(String jobId) throws Exception {
        return JsonUtil.readFromJsonFile(dataDir.resolve("JOB_" + jobId + ".json").toString(), Job.class);
    }

    public List<Job> getAllJobs() throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        java.io.File dir = dataDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return jobs;
        }
        java.io.File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (java.io.File file : files) {
                Job job = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Job.class);
                if (job != null) {
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    public List<Job> findByMoId(String moId) throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        for (Job job : getAllJobs()) {
            if (moId.equals(job.getMoId())) {
                jobs.add(job);
            }
        }
        return jobs;
    }
}

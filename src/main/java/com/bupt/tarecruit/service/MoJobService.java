package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.JobRepository;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MoJobService {
    private final JobRepository jobRepository = new JobRepository();
    private static final String JOB_DIR = "data/jobs/";

    // ✅ 修复：直接接收 moId 和 moName（匹配你的登录体系）
    public void publishMoJob(Job job, String currentMoId, String mdName) throws Exception {
        String jobId = UUID.randomUUID().toString();
        job.setJobId(jobId);
        
        // 自动赋值
        job.setMoId(currentMoId);
        job.setMdId(currentMoId);    // 负责人ID
        job.setMdName(mdName);      // 负责人姓名

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        job.setPublishedAt(LocalDateTime.now().format(formatter));
        job.setStatus("OPEN");
        jobRepository.saveJob(job);
    }

    // ===================== 以下代码完全不变 =====================
    public List<Job> getJobsByMoId(String currentMoId) throws Exception {
        List<Job> allJobs = jobRepository.getAllJobs();
        List<Job> moJobs = allJobs.stream()
                .filter(job -> currentMoId.equals(job.getMoId()))
                .collect(Collectors.toList());

        Collections.sort(moJobs, Comparator.comparing(Job::getPublishedAt).reversed());
        return moJobs;
    }

    public Job getJobById(String jobId, String currentMoId) throws Exception {
        Job job = jobRepository.getJobById(jobId);
        if (job == null || !currentMoId.equals(job.getMoId())) {
            throw new Exception("无权限操作此岗位");
        }
        return job;
    }

    public void updateJob(Job job) throws Exception {
        jobRepository.saveJob(job);
    }

    public void downJob(String jobId, String currentMoId) throws Exception {
        Job job = getJobById(jobId, currentMoId);
        job.setStatus("CLOSED");
        updateJob(job);
    }

    public void deleteJob(String jobId, String currentMoId) throws Exception {
        getJobById(jobId, currentMoId);
        File file = new File(JOB_DIR + "JOB_" + jobId + ".json");
        if (file.exists()) file.delete();
    }
}
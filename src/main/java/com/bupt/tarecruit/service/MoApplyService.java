package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MoApplyService {
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final JobRepository jobRepository = new JobRepository();

    // 获取当前MO的所有申请（按时间倒序）
    public List<Application> getApplicationsForMO(String moId) throws Exception {
        List<Job> moJobs = jobRepository.getAllJobs().stream()
                .filter(job -> moId.equals(job.getMoId()))
                .collect(Collectors.toList());
        List<String> jobIds = moJobs.stream().map(Job::getJobId).collect(Collectors.toList());
        List<Application> apps = applicationRepository.getApplicationsByMoId(jobIds);
        apps.sort((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()));
        return apps;
    }

    // 按jobId查询申请
    public List<Application> getApplicationsByJobId(String jobId) throws Exception {
        return applicationRepository.getApplicationsByJobId(jobId);
    }

    // 统计待处理申请
    public int countPendingApplications(String moId) throws Exception {
        return (int) getApplicationsForMO(moId).stream()
                .filter(app -> "PENDING".equalsIgnoreCase(app.getStatus()))
                .count();
    }

    // ===================== ✅ 新增：根据ID查询单个申请 =====================
    public Application getApplicationById(String appId) throws Exception {
        return applicationRepository.getApplicationById(appId);
    }

    // ===================== ✅ 新增：更新申请（状态+类型+交流消息） =====================
    // ===================== 修复：更新申请（调用正确的save方法） =====================
public void updateApplication(Application app) throws Exception {
    app.setMarkTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    // 用你Repository里的save方法，而不是saveApplication
    applicationRepository.save(app);
}






    private final ApplicationRepository appRepository = new ApplicationRepository();

    // 1. 查询【当前MO自己岗位】的所有申请（权限核心）
    public List<Application> getAppListByJobId(String jobId, String currentMoId) throws Exception {
        // 校验：岗位必须属于当前MO
        var job = jobRepository.getJobById(jobId);
        if (job == null || !currentMoId.equals(job.getMoId())) {
            throw new Exception("无权限查看该岗位的申请");
        }
        // 返回该岗位的所有申请
        return appRepository.getApplicationsByJobId(jobId);
    }

    // 2. 更新申请状态 + 反馈
    

    // 3. 根据ID查询申请
    public Application getAppById(String appId) throws Exception {
        return appRepository.getApplicationById(appId);
    }
}

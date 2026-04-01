package com.bupt.tarecruit.model;

public class Job {
    private String jobId;
    private String moId;
    private String moduleName;
    private String jobType;
    private String requirements;
    private int weeklyWorkload;
    private String deadline;
    private String status; // "OPEN", "CLOSED"
    private String publishedAt;

    public Job() {}

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public int getWeeklyWorkload() { return weeklyWorkload; }
    public void setWeeklyWorkload(int weeklyWorkload) { this.weeklyWorkload = weeklyWorkload; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
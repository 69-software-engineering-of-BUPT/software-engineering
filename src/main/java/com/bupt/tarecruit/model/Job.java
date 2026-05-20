package com.bupt.tarecruit.model;

public class Job {
    private String moId;
    private String jobId;
    private String mdId;
    private String mdName;
    private String moduleName;
    private String jobType;
    private String requirements;
    private String introduction;
    private int weeklyWorkload;
    private String deadline;
    private String status;
    private String publishedAt;

    // 新增招聘人数
    private int leaderCount;
    private int memberCount;

    public Job() {}

    public int getLeaderCount() { return leaderCount; }
    public void setLeaderCount(int leaderCount) { this.leaderCount = leaderCount; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    // 原有 Getter/Setter
    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getMdId() { return mdId; }
    public void setMdId(String mdId) { this.mdId = mdId; }
    public String getMdName() { return mdName; }
    public void setMdName(String mdName) { this.mdName = mdName; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public int getWeeklyWorkload() { return weeklyWorkload; }
    public void setWeeklyWorkload(int weeklyWorkload) { this.weeklyWorkload = weeklyWorkload; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
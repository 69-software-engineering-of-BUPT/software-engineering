package com.bupt.tarecruit.model;

/**
 * Entity class for Job information.
 * Updated to support ApplicationService and TA004 requirements.
 */
public class Job {
    private String jobId;
    private String mdId;
    private String mdName;        // Name of the Module Organizer
    private String moduleName;    // The name of the course module
    private String jobType;       // e.g. "Teaching Assistant", "Lab Assistant"
    private String requirements;
    private String introduction;
    private int weeklyWorkload;   // Hours per week
    private String deadline;      // Application deadline (yyyy-MM-dd)
    private String status;        // "OPEN", "CLOSED"
    private String publishedAt;

    public Job() {}

    // Getters and Setters
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

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }

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
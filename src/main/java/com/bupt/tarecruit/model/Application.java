package com.bupt.tarecruit.model;

public class Application {
    private String applicationId;
    private String jobId;
    private String taId;
    private String appliedAt;
    private String statement; // TA005: Application Statement
    private String status; // "PENDING", "APPROVED", "REJECTED", "INTERVIEW"
    private String feedback;

    public Application() {}

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
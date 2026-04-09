package com.bupt.tarecruit.model;

import com.google.gson.annotations.SerializedName;

public class Application {
    private String applicationId;
    private String jobId;
    @SerializedName(value = "studentId", alternate = {"taId"})
    private String studentId;
    private String status;
    @SerializedName(value = "applyTime", alternate = {"appliedAt"})
    private String applyTime;
    private String statement;
    private String feedback;
    @SerializedName(value = "markedBy", alternate = {"mdId"})
    private String markedBy;
    private String markTime;
    private String applicationType;
    private boolean cvAttached;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }

    public String getMarkTime() { return markTime; }
    public void setMarkTime(String markTime) { this.markTime = markTime; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }

    public boolean isCvAttached() { return cvAttached; }
    public void setCvAttached(boolean cvAttached) { this.cvAttached = cvAttached; }
}

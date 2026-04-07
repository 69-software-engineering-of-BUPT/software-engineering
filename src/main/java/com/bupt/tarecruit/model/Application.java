package com.bupt.tarecruit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Entity class for TA Job Application.
 * This model represents the raw data stored in data/applications/ JSON files.
 */
public class Application {
    private String applicationId; // Unique ID (UUID)
    private String jobId;         // Reference to the Job position
    @SerializedName("taId")
    private String studentId;     // Reference to the Applicant (Student ID)
    private String status;        // PENDING, APPROVED, REJECTED, INTERVIEW
    @SerializedName("appliedAt")
    private String applyTime;     // Timestamp of submission (ISO or yyyy-MM-dd HH:mm:ss)
    private String statement;     // Personal statement or cover letter
    private String feedback;      // Comments from the Module Organizer
    private String mdId;      // ID of the person who updated the status
    private String markTime;      // Timestamp of the last status update
    private String applicationType; // Type of application (leader/non-leader)

    public Application() {}

    // Getters and Setters
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

    public String getMarkedBy() { return mdId; }
    public void setMarkedBy(String markedBy) { this.mdId = markedBy; }

    public String getMarkTime() { return markTime; }
    public void setMarkTime(String markTime) { this.markTime = markTime; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }
}
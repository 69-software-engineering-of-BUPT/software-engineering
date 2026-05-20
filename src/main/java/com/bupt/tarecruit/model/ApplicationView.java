package com.bupt.tarecruit.model;

/**
 * Data Transfer Object (DTO) for TA004.
 * Used to combine Application status with Job details for display on the UI.
 */
public class ApplicationView {
    private String jobId;
    private String moduleName;
    private String mdName;
    private String status;       // Application status: PENDING, APPROVED, etc.
    private String applyTime;    // Timestamp of the submission
    private String applicationType; 
    private String applicationId;
    private String feedback;
    /** Original application text plus optional TA follow-up lines (not shown in the table, used in the feedback dialog). */
    private String statement;
    private String taId;         // TA's user ID (used in MO review view)
    private boolean cvAttached;  // Whether TA chose to attach their CV
    private String cvFilePath;   // Actual path to TA's CV file (populated in MO view)

    public ApplicationView() {}

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getMdName() { return mdName; }
    public void setMdName(String mdName) { this.mdName = mdName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public boolean isCvAttached() { return cvAttached; }
    public void setCvAttached(boolean cvAttached) { this.cvAttached = cvAttached; }

    public String getCvFilePath() { return cvFilePath; }
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }
}
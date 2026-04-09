package com.bupt.tarecruit.model;

public class ApplicationView {
    private String jobId;
    private String moduleName;
    private String mdName;
    private String status;
    private String applyTime;
    private String applicationType;
    private String applicationId;
    private String feedback;
    private String statement;
    private String studentId;
    private String studentName;
    private boolean cvAttached;
    private String cvFilePath;

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

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getTaId() { return studentId; }
    public void setTaId(String taId) { this.studentId = taId; }

    public boolean isCvAttached() { return cvAttached; }
    public void setCvAttached(boolean cvAttached) { this.cvAttached = cvAttached; }

    public String getCvFilePath() { return cvFilePath; }
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }
}

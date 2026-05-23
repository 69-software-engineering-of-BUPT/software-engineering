package com.bupt.tarecruit.model;

import java.util.ArrayList;
import java.util.List;

public class ConversationThread {
    private String applicationId;
    private String jobId;
    private String moduleName;
    private String taId;
    private String status;
    private String applicationType;
    private String applyTime;
    private String feedback;
    private boolean needsMoReply;
    private final List<ConversationMessage> messages = new ArrayList<>();

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }

    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public boolean isNeedsMoReply() { return needsMoReply; }
    public void setNeedsMoReply(boolean needsMoReply) { this.needsMoReply = needsMoReply; }

    public List<ConversationMessage> getMessages() { return messages; }

    public ConversationMessage getLatestMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    public String getLatestMessageTime() {
        ConversationMessage latest = getLatestMessage();
        return latest == null ? applyTime : latest.getSentAt();
    }
}

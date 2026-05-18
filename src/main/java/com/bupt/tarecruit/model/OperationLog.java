package com.bupt.tarecruit.model;

public class OperationLog {
    private String logId;
    private String createdAt;
    private String actorId;
    private String actorName;
    private String actorRole;
    private String actionType;
    private String actionLabel;
    private String targetType;
    private String targetId;
    private String targetName;
    private String result;
    private String resultClass;
    private String message;

    public OperationLog() {}

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getResultClass() { return resultClass; }
    public void setResultClass(String resultClass) { this.resultClass = resultClass; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

package com.bupt.tarecruit.model;

public class Notification {
    private String notificationId;
    private String taId;
    private String type; // "STATUS_UPDATE", "FEEDBACK"
    private String content;
    private String relatedApplicationId;
    private String createdAt;
    private boolean isRead;

    public Notification() {}

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRelatedApplicationId() { return relatedApplicationId; }
    public void setRelatedApplicationId(String relatedApplicationId) { this.relatedApplicationId = relatedApplicationId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
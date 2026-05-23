package com.bupt.tarecruit.model;

public class ConversationMessage {
    private String sender;
    private String sentAt;
    private String content;

    public ConversationMessage() {}

    public ConversationMessage(String sender, String sentAt, String content) {
        this.sender = sender;
        this.sentAt = sentAt;
        this.content = content;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isFromTA() {
        return "TA".equalsIgnoreCase(sender);
    }

    public boolean isFromMO() {
        return "MO".equalsIgnoreCase(sender);
    }
}

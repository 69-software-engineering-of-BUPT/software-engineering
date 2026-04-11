package com.bupt.tarecruit.model;

public class User {
    private String userId; // e.g., "231225959"
    private String password;
    private String role; // "TA", "MO", "ADMIN"
    private String name;

    private String cvFilePath;
    private int activeJobsCount;

    // TA profile fields
    private String email;
    private String phoneNumber;
    private String researchArea;
    private String cet6Grade;

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCvFilePath() { return cvFilePath; }
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }
    public int getActiveJobsCount() { return activeJobsCount; }
    public void setActiveJobsCount(int activeJobsCount) { this.activeJobsCount = activeJobsCount; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    public String getCet6Grade() { return cet6Grade; }
    public void setCet6Grade(String cet6Grade) { this.cet6Grade = cet6Grade; }
}
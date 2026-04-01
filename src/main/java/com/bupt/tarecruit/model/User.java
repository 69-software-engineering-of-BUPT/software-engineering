package com.bupt.tarecruit.model;

import java.util.List;

public class User {
    private String userId; // e.g., "231225959"
    private String password;
    private String role; // "TA", "MO", "ADMIN"
    private String name;
    
    // TA specific fields
    private String major;
    private String grade;
    private List<String> completedModules;
    private String gpa;
    private String experience;
    private String cvFilePath;
    private int activeJobsCount; // For AD001: limit to 3

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public List<String> getCompletedModules() { return completedModules; }
    public void setCompletedModules(List<String> completedModules) { this.completedModules = completedModules; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCvFilePath() { return cvFilePath; }
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }
    public int getActiveJobsCount() { return activeJobsCount; }
    public void setActiveJobsCount(int activeJobsCount) { this.activeJobsCount = activeJobsCount; }
}
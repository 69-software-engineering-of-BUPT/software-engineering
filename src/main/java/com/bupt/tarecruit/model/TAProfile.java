package com.bupt.tarecruit.model;

/**
 * Entity for TA001: Comprehensive Personal Profile
 */
public class TAProfile {
    private String studentId;    // Mandatory
    private String fullName;     // Mandatory
    private String email;        // Mandatory
    private String phoneNumber;  // Mandatory
    private String researchArea; // Mandatory
    private String cet6Grade;    // Mandatory

    public TAProfile() {}

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }

    public String getCet6Grade() { return cet6Grade; }
    public void setCet6Grade(String cet6Grade) { this.cet6Grade = cet6Grade; }
}
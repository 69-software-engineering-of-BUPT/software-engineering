package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.repository.TAProfileRepository;

public class TAProfileService {
    final private TAProfileRepository repository = new TAProfileRepository();

    /**
     * Process profile update with mandatory field validation
     */
    public void updateProfile(TAProfile profile) throws Exception {
        // Validation logic for Acceptance Criteria 2
        validate(profile);
        repository.save(profile);
    }

    /**
     * Get profile data for the current user
     */
    public TAProfile getProfile(String studentId) throws Exception {
        return repository.findById(studentId);
    }

    private void validate(TAProfile p) {
        if (isEmpty(p.getStudentId())) throw new RuntimeException("Student ID is mandatory");
        if (isEmpty(p.getFullName())) throw new RuntimeException("Full Name is mandatory");
        if (isEmpty(p.getEmail())) throw new RuntimeException("Email is mandatory");
        if (isEmpty(p.getPhoneNumber())) throw new RuntimeException("Phone Number is mandatory");
        if (isEmpty(p.getResearchArea())) throw new RuntimeException("Research Area is mandatory");
        if (isEmpty(p.getCet6Grade())) throw new RuntimeException("CET6 Grade is mandatory");
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
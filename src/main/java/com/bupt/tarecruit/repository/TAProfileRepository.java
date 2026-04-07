package com.bupt.tarecruit.repository;

import java.io.File;

import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.util.JsonUtil;

public class TAProfileRepository {
    // Data is stored in data/users/ directory according to project structure
    private static final String DATA_PATH = "data/users/";

    /**
     * Save profile as studentId_profile.json
     */
    public void save(TAProfile profile) throws Exception {
        String fileName = DATA_PATH + profile.getStudentId() + "_profile.json";
        
        File dir = new File(DATA_PATH);
        if (!dir.exists()) dir.mkdirs();

        // Use JsonUtil to persist the object into a JSON file
        JsonUtil.saveToJsonFile(profile, fileName);
    }

    /**
     * Retrieve profile by studentId
     */
    public TAProfile findById(String studentId) throws Exception {
        String fileName = DATA_PATH + studentId + "_profile.json";
        File file = new File(fileName);
        if (!file.exists()) return null;
        
        return JsonUtil.readFromJsonFile(fileName, TAProfile.class);
    }
}
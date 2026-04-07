package com.bupt.tarecruit.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.util.JsonUtil;

public class ApplicationRepository {
    private static final String DATA_PATH = "data/applications/";

    public void save(Application app) throws Exception {
        File dir = new File(DATA_PATH);
        if (!dir.exists()) dir.mkdirs();
        String primary = DATA_PATH + app.getApplicationId() + "_application.json";
        File primaryFile = new File(primary);
        if (primaryFile.exists()) {
            JsonUtil.saveToJsonFile(app, primary);
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Application existing = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                if (existing != null && app.getApplicationId().equals(existing.getApplicationId())) {
                    JsonUtil.saveToJsonFile(app, file.getAbsolutePath());
                    return;
                }
            }
        }
        JsonUtil.saveToJsonFile(app, primary);
    }

    public Application findById(String applicationId) throws Exception {
        String fileName = DATA_PATH + applicationId + "_application.json";
        File direct = new File(fileName);
        if (direct.exists()) {
            return JsonUtil.readFromJsonFile(fileName, Application.class);
        }
        File dir = new File(DATA_PATH);
        if (!dir.exists()) return null;
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return null;
        for (File file : files) {
            Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
            if (app != null && applicationId.equals(app.getApplicationId())) {
                return app;
            }
        }
        return null;
    }

    public List<Application> findByStudentId(String studentId) throws Exception {
        List<Application> result = new ArrayList<>();
        File dir = new File(DATA_PATH);
        if (!dir.exists()) return result;

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                // Filter by studentId
                if (app != null && studentId.equals(app.getStudentId())) {
                    result.add(app);
                }
            }
        }
        return result;
    }
}
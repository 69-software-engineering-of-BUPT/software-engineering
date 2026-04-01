package com.bupt.tarecruit.repository;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {
    private static final String DATA_DIR = "data/applications/";

    public void saveApplication(Application app) throws IOException {
        String filePath = DATA_DIR + "APP_" + app.getApplicationId() + ".json";
        JsonUtil.saveToJsonFile(app, filePath);
    }

    public Application getApplicationById(String appId) throws IOException {
        String filePath = DATA_DIR + "APP_" + appId + ".json";
        return JsonUtil.readFromJsonFile(filePath, Application.class);
    }

    public List<Application> getApplicationsByJobId(String jobId) throws IOException {
        List<Application> apps = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                    if (app != null && jobId.equals(app.getJobId())) {
                        apps.add(app);
                    }
                }
            }
        }
        return apps;
    }

    public List<Application> getApplicationsByTaId(String taId) throws IOException {
        List<Application> apps = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                    if (app != null && taId.equals(app.getTaId())) {
                        apps.add(app);
                    }
                }
            }
        }
        return apps;
    }
}
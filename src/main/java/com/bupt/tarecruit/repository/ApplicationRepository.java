package com.bupt.tarecruit.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.util.JsonUtil;

public class ApplicationRepository {
    private static final String DATA_PATH = "data/applications/";
    private static final String DATA_DIR = "data/applications/";

    public Application getApplicationById(String appId) throws IOException {
        // 错误写法：String filePath = DATA_DIR + "APP_" + appId + ".json";
        // 正确写法（你的文件是：xxx_application.json）
        String filePath = DATA_DIR + appId + "_application.json";
        return JsonUtil.readFromJsonFile(filePath, Application.class);
    }

    // 以下代码**完全保留不动**，只改了上面一个方法
    public void saveApplication(Application app) throws IOException {
        String filePath = DATA_DIR + app.getApplicationId() + "_application.json";
        JsonUtil.saveToJsonFile(app, filePath);
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

    public List<Application> getApplicationsByMoId(List<String> jobIds) throws IOException {
        List<Application> apps = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                    if (app != null && jobIds.contains(app.getJobId())) {
                        apps.add(app);
                    }
                }
            }
        }
        return apps;
    }





    
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

    public List<Application> findAll() throws Exception {
        List<Application> result = new ArrayList<>();
        File dir = new File(DATA_PATH);
        if (!dir.exists()) return result;
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                if (app != null) result.add(app);
            }
        }
        return result;
    }
}
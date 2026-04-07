package com.bupt.tarecruit.repository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.util.JsonUtil;

public class ApplicationRepository {
    private final Path dataDir;

    public ApplicationRepository() {
        this(Paths.get("."));
    }

    public ApplicationRepository(Path root) {
        this.dataDir = root.resolve("data").resolve("applications");
    }

    public void save(Application app) throws Exception {
        File dir = dataDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String canonical = dataDir.resolve("APP_" + app.getApplicationId() + ".json").toString();
        Application existing = findById(app.getApplicationId());
        if (existing != null) {
            JsonUtil.saveToJsonFile(app, canonical);
            return;
        }
        JsonUtil.saveToJsonFile(app, canonical);
    }

    public Application findById(String applicationId) throws Exception {
        String[] candidates = new String[] {
            dataDir.resolve("APP_" + applicationId + ".json").toString(),
            dataDir.resolve(applicationId + "_application.json").toString()
        };
        for (String candidate : candidates) {
            Application app = JsonUtil.readFromJsonFile(candidate, Application.class);
            if (app != null) {
                return app;
            }
        }
        File dir = dataDir.toFile();
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                if (app != null && applicationId.equals(app.getApplicationId())) {
                    return app;
                }
            }
        }
        return null;
    }

    public List<Application> findByStudentId(String studentId) throws Exception {
        List<Application> result = new ArrayList<Application>();
        for (Application app : getAll()) {
            if (studentId.equals(app.getStudentId())) {
                result.add(app);
            }
        }
        return result;
    }

    public List<Application> findByJobId(String jobId) throws Exception {
        List<Application> result = new ArrayList<Application>();
        for (Application app : getAll()) {
            if (jobId.equals(app.getJobId())) {
                result.add(app);
            }
        }
        return result;
    }

    public boolean existsByStudentIdAndJobId(String studentId, String jobId) throws Exception {
        for (Application app : getAll()) {
            if (studentId.equals(app.getStudentId()) && jobId.equals(app.getJobId())) {
                return true;
            }
        }
        return false;
    }

    public List<Application> getAll() throws Exception {
        List<Application> result = new ArrayList<Application>();
        File dir = dataDir.toFile();
        if (!dir.exists()) {
            return result;
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Application app = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Application.class);
                if (app != null) {
                    result.add(app);
                }
            }
        }
        return result;
    }
}

package com.bupt.tarecruit.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bupt.tarecruit.model.OperationLog;
import com.bupt.tarecruit.util.JsonUtil;

public class OperationLogRepository {
    private static final String DATA_DIR = "data/operation-logs/";

    public void save(OperationLog log) throws IOException {
        String filePath = DATA_DIR + "LOG_" + log.getLogId() + ".json";
        JsonUtil.saveToJsonFile(log, filePath);
    }

    public List<OperationLog> getAllLogs() throws IOException {
        List<OperationLog> logs = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("LOG_") && name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    OperationLog log = JsonUtil.readFromJsonFile(file.getAbsolutePath(), OperationLog.class);
                    if (log != null) {
                        logs.add(log);
                    }
                }
            }
        }

        logs.sort(Comparator.comparing(OperationLog::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed());
        return logs;
    }
}

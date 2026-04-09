package com.bupt.tarecruit.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.util.JsonUtil;

public class NotificationRepository {
    private static final String DATA_DIR = "data/notifications/";

    public void saveNotification(Notification notification) throws IOException {
        String filePath = DATA_DIR + "NOTI_" + notification.getNotificationId() + ".json";
        JsonUtil.saveToJsonFile(notification, filePath);
    }

    public List<Notification> getAllNotifications() throws IOException {
        List<Notification> notifications = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    Notification notification = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
            }
        }
        return notifications;
    }
}

package com.bupt.tarecruit.repository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.util.JsonUtil;

public class NotificationRepository {
    private final Path dataDir;

    public NotificationRepository() {
        this(Paths.get("."));
    }

    public NotificationRepository(Path root) {
        this.dataDir = root.resolve("data").resolve("notifications");
    }

    public void saveNotification(Notification notification) throws java.io.IOException {
        JsonUtil.saveToJsonFile(notification, dataDir.resolve("NOTI_" + notification.getNotificationId() + ".json").toString());
    }

    public List<Notification> getAllNotifications() throws java.io.IOException {
        List<Notification> notifications = new ArrayList<Notification>();
        File dir = dataDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return notifications;
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                Notification notification = JsonUtil.readFromJsonFile(file.getAbsolutePath(), Notification.class);
                if (notification != null) {
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }

    public List<Notification> findByTaId(String taId) throws java.io.IOException {
        List<Notification> notifications = new ArrayList<Notification>();
        for (Notification notification : getAllNotifications()) {
            if (taId.equals(notification.getTaId())) {
                notifications.add(notification);
            }
        }
        return notifications;
    }
}

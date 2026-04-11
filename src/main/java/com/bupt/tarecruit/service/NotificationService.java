package com.bupt.tarecruit.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.repository.NotificationRepository;

public class NotificationService {
    private final NotificationRepository repo = new NotificationRepository();

    public List<Notification> getNotificationsForTA(String taId) throws IOException {
        List<Notification> all = repo.getAllNotifications();
        List<Notification> result = new ArrayList<>();
        for (Notification n : all) {
            if (taId.equals(n.getTaId())) {
                result.add(n);
            }
        }
        result.sort(Comparator.comparing(Notification::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public int getUnreadCount(String taId) throws IOException {
        List<Notification> list = getNotificationsForTA(taId);
        int count = 0;
        for (Notification n : list) {
            if (!n.isRead()) count++;
        }
        return count;
    }

    public void markAsRead(String notificationId) throws IOException {
        List<Notification> all = repo.getAllNotifications();
        for (Notification n : all) {
            if (notificationId.equals(n.getNotificationId())) {
                n.setRead(true);
                repo.saveNotification(n);
                return;
            }
        }
    }

    public void markAsUnread(String notificationId) throws IOException {
        List<Notification> all = repo.getAllNotifications();
        for (Notification n : all) {
            if (notificationId.equals(n.getNotificationId())) {
                n.setRead(false);
                repo.saveNotification(n);
                return;
            }
        }
    }

    public void markAllRead(String taId) throws IOException {
        List<Notification> all = repo.getAllNotifications();
        for (Notification n : all) {
            if (taId.equals(n.getTaId()) && !n.isRead()) {
                n.setRead(true);
                repo.saveNotification(n);
            }
        }
    }

    public void createNotification(String taId, String type, String content, String relatedAppId) throws IOException {
        Notification n = new Notification();
        n.setNotificationId(UUID.randomUUID().toString().substring(0, 8));
        n.setTaId(taId);
        n.setType(type);
        n.setContent(content);
        n.setRelatedApplicationId(relatedAppId);
        n.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        n.setRead(false);
        repo.saveNotification(n);
    }
}

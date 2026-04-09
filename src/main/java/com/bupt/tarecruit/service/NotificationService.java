package com.bupt.tarecruit.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.repository.NotificationRepository;
import com.bupt.tarecruit.util.IdGenerator;
import com.bupt.tarecruit.util.TimeUtil;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService() {
        this(Paths.get("."));
    }

    public NotificationService(Path root) {
        this.notificationRepository = new NotificationRepository(root);
    }

    public void createStatusUpdate(String taId, String applicationId, String content) throws IOException {
        createNotification(taId, "STATUS_UPDATE", content, applicationId);
    }

    public void createNotification(String taId, String type, String content, String relatedApplicationId) throws IOException {
        Notification notification = new Notification();
        notification.setNotificationId(IdGenerator.newId("NOTI"));
        notification.setTaId(taId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedApplicationId(relatedApplicationId);
        notification.setCreatedAt(TimeUtil.nowDateTime());
        notification.setRead(false);
        notificationRepository.saveNotification(notification);
    }

    public List<Notification> getNotificationsForTa(String taId) throws IOException {
        List<Notification> notifications = new ArrayList<Notification>(notificationRepository.findByTaId(taId));
        notifications.sort(Comparator.comparing(Notification::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return notifications;
    }

    public List<Notification> getNotificationsForTA(String taId) throws IOException {
        return getNotificationsForTa(taId);
    }

    public int getUnreadCount(String taId) throws IOException {
        int count = 0;
        for (Notification notification : getNotificationsForTa(taId)) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }

    public void markAsRead(String notificationId) throws IOException {
        updateReadState(notificationId, true);
    }

    public void markAsUnread(String notificationId) throws IOException {
        updateReadState(notificationId, false);
    }

    public void markAllRead(String taId) throws IOException {
        for (Notification notification : notificationRepository.getAllNotifications()) {
            if (taId.equals(notification.getTaId()) && !notification.isRead()) {
                notification.setRead(true);
                notificationRepository.saveNotification(notification);
            }
        }
    }

    private void updateReadState(String notificationId, boolean isRead) throws IOException {
        for (Notification notification : notificationRepository.getAllNotifications()) {
            if (notificationId.equals(notification.getNotificationId())) {
                notification.setRead(isRead);
                notificationRepository.saveNotification(notification);
                return;
            }
        }
    }
}

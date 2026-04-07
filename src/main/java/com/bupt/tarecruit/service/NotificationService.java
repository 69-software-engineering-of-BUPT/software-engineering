package com.bupt.tarecruit.service;

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

    public void createStatusUpdate(String taId, String applicationId, String content) throws Exception {
        Notification notification = new Notification();
        notification.setNotificationId(IdGenerator.newId("NOTI"));
        notification.setTaId(taId);
        notification.setType("STATUS_UPDATE");
        notification.setContent(content);
        notification.setRelatedApplicationId(applicationId);
        notification.setCreatedAt(TimeUtil.nowDateTime());
        notification.setRead(false);
        notificationRepository.saveNotification(notification);
    }

    public List<Notification> getNotificationsForTa(String taId) throws Exception {
        List<Notification> notifications = new ArrayList<Notification>(notificationRepository.findByTaId(taId));
        notifications.sort(Comparator.comparing(Notification::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return notifications;
    }
}

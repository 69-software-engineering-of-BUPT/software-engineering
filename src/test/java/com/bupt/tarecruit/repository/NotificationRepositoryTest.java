package com.bupt.tarecruit.repository;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.bupt.tarecruit.model.Notification;

public class NotificationRepositoryTest {
    private final NotificationRepository notificationRepository = new NotificationRepository();

    @Test
    public void getAllNotificationsLoadsSeedNotifications() throws Exception {
        List<Notification> notifications = notificationRepository.getAllNotifications();

        assertFalse(notifications.isEmpty());
    }

    @Test
    public void getAllNotificationsContainsTA001Notification() throws Exception {
        List<Notification> notifications = notificationRepository.getAllNotifications();

        boolean found = false;
        for (Notification n : notifications) {
            if ("TA001".equals(n.getTaId())) {
                found = true;
                assertNotNull(n.getNotificationId());
                assertNotNull(n.getContent());
                break;
            }
        }
        assertTrue("Expected at least one notification for TA001", found);
    }

    @Test
    public void getAllNotificationsContainsStatusUpdateType() throws Exception {
        List<Notification> notifications = notificationRepository.getAllNotifications();

        boolean found = false;
        for (Notification n : notifications) {
            if ("STATUS_UPDATE".equals(n.getType())) {
                found = true;
                break;
            }
        }
        assertTrue("Expected at least one STATUS_UPDATE notification", found);
    }
}

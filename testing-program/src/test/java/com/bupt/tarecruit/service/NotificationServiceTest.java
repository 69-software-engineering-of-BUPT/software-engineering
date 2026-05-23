package com.bupt.tarecruit.service;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.repository.NotificationRepository;

public class NotificationServiceTest {
    private final NotificationService notificationService = new NotificationService();
    private final NotificationRepository notifRepo = new NotificationRepository();

    @Test
    public void getNotificationsForTA001ReturnsTaOwnedNotifications() throws Exception {
        List<Notification> notifications = notificationService.getNotificationsForTA("TA001");

        assertFalse(notifications.isEmpty());
        for (Notification n : notifications) {
            assertEquals("TA001", n.getTaId());
        }
    }

    @Test
    public void getNotificationsForTA001ContainsStatusUpdateType() throws Exception {
        List<Notification> notifications = notificationService.getNotificationsForTA("TA001");

        boolean hasStatusUpdate = false;
        for (Notification n : notifications) {
            if ("STATUS_UPDATE".equals(n.getType())) {
                hasStatusUpdate = true;
                break;
            }
        }
        assertTrue("Expected at least one STATUS_UPDATE notification for TA001", hasStatusUpdate);
    }

    @Test
    public void getNotificationsForNonExistentTaReturnsEmptyList() throws Exception {
        List<Notification> notifications = notificationService.getNotificationsForTA("TA_NONE");

        assertTrue(notifications.isEmpty());
    }

    @Test
    public void getUnreadCountReturnsZeroForTA001WhenAllNotificationsAreRead() throws Exception {
        // Seed data: both NOTI0001 and NOTI0002 for TA001 have isRead = true
        int unread = notificationService.getUnreadCount("TA001");

        assertEquals(0, unread);
    }

    @Test
    public void getNotificationsForTA001AreSortedByCreatedAtDescending() throws Exception {
        List<Notification> notifications = notificationService.getNotificationsForTA("TA001");

        for (int i = 0; i < notifications.size() - 1; i++) {
            String current = notifications.get(i).getCreatedAt();
            String next = notifications.get(i + 1).getCreatedAt();
            if (current != null && next != null) {
                assertFalse(
                    "Notifications should be sorted newest first",
                    current.compareTo(next) < 0
                );
            }
        }
    }

    // ------------------------------------------------------------------ //
    // TA006: mark operations — markAsRead, markAsUnread, markAllRead     //
    // Uses isolated TA_TEST_MARK to avoid modifying TA001 seed data      //
    // ------------------------------------------------------------------ //

    @Test
    public void markAsReadChangesNotificationReadStatusToTrue() throws Exception {
        Notification testNotif = new Notification();
        testNotif.setNotificationId("TESTMARK01");
        testNotif.setTaId("TA_TEST_MARK");
        testNotif.setType("TEST");
        testNotif.setContent("Test notification for markAsRead");
        testNotif.setCreatedAt("2026-01-01 00:00:00");
        testNotif.setRead(false);
        notifRepo.saveNotification(testNotif);

        try {
            notificationService.markAsRead("TESTMARK01");

            List<Notification> list = notificationService.getNotificationsForTA("TA_TEST_MARK");
            assertEquals(1, list.size());
            assertTrue("Notification should be marked as read", list.get(0).isRead());
        } finally {
            new java.io.File("data/notifications/NOTI_TESTMARK01.json").delete();
        }
    }

    @Test
    public void markAsUnreadChangesNotificationReadStatusToFalse() throws Exception {
        Notification testNotif = new Notification();
        testNotif.setNotificationId("TESTMARK02");
        testNotif.setTaId("TA_TEST_MARK");
        testNotif.setType("TEST");
        testNotif.setContent("Test notification for markAsUnread");
        testNotif.setCreatedAt("2026-01-01 00:00:00");
        testNotif.setRead(true);
        notifRepo.saveNotification(testNotif);

        try {
            notificationService.markAsUnread("TESTMARK02");

            List<Notification> list = notificationService.getNotificationsForTA("TA_TEST_MARK");
            assertEquals(1, list.size());
            assertFalse("Notification should be marked as unread", list.get(0).isRead());
        } finally {
            new java.io.File("data/notifications/NOTI_TESTMARK02.json").delete();
        }
    }

    @Test
    public void markAllReadSetsAllUnreadNotificationsForTaToRead() throws Exception {
        Notification n1 = new Notification();
        n1.setNotificationId("TESTMARK03");
        n1.setTaId("TA_TEST_MARK");
        n1.setType("TEST");
        n1.setContent("Unread notification A");
        n1.setCreatedAt("2026-01-01 00:00:01");
        n1.setRead(false);

        Notification n2 = new Notification();
        n2.setNotificationId("TESTMARK04");
        n2.setTaId("TA_TEST_MARK");
        n2.setType("TEST");
        n2.setContent("Unread notification B");
        n2.setCreatedAt("2026-01-01 00:00:02");
        n2.setRead(false);

        notifRepo.saveNotification(n1);
        notifRepo.saveNotification(n2);

        try {
            notificationService.markAllRead("TA_TEST_MARK");

            List<Notification> list = notificationService.getNotificationsForTA("TA_TEST_MARK");
            assertEquals(2, list.size());
            for (Notification n : list) {
                assertTrue("All notifications should be read after markAllRead", n.isRead());
            }
        } finally {
            new java.io.File("data/notifications/NOTI_TESTMARK03.json").delete();
            new java.io.File("data/notifications/NOTI_TESTMARK04.json").delete();
        }
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.service.NotificationService;

/**
 * TA006: View and manage notifications.
 */
@WebServlet("/ta/notifications")
public class TANotificationServlet extends HttpServlet {
    private final NotificationService notificationService = new NotificationService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Notification> notifications = notificationService.getNotificationsForTA(studentId);
        int unreadCount = notificationService.getUnreadCount(studentId);

        req.setAttribute("notifications", notifications);
        req.setAttribute("notificationsJson", gson.toJson(notifications));
        req.setAttribute("unreadCount", unreadCount);
        req.setAttribute("studentId", studentId);
        req.getRequestDispatcher("/jsp/ta/notifications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("markRead".equals(action)) {
            String notiId = req.getParameter("notificationId");
            if (notiId != null) {
                notificationService.markAsRead(notiId.trim());
            }
        } else if ("markUnread".equals(action)) {
            String notiId = req.getParameter("notificationId");
            if (notiId != null) {
                notificationService.markAsUnread(notiId.trim());
            }
        } else if ("markAllRead".equals(action)) {
            notificationService.markAllRead(studentId);
        }

        resp.sendRedirect(req.getContextPath() + "/ta/notifications");
    }
}

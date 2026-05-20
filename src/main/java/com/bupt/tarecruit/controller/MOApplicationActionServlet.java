package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.NotificationService;
import com.bupt.tarecruit.util.ApplicationNotificationUtil;

/**
 * Compatibility endpoint for older MO application cards.
 * It uses the same ApplicationService + NotificationService flow as the detail page.
 */
@WebServlet("/mo/application/action")
public class MOApplicationActionServlet extends HttpServlet {
    private final ApplicationService appService = new ApplicationService();
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied: MO role required.");
            return;
        }

        String moId = (String) session.getAttribute("userAccount");
        String appId = trimToNull(req.getParameter("applicationId"));
        if (appId == null) {
            session.setAttribute("moActionError", "Application ID is required.");
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
            return;
        }

        try {
            String status = req.getParameter("status");
            String feedback = req.getParameter("feedback");
            String applicationType = req.getParameter("applicationType");
            String moMessage = req.getParameter("moMessage");

            Application app = appService.updateApplicationStatus(appId, status, feedback, moId, applicationType);

            if (trimToNull(moMessage) != null) {
                appService.appendMOMessage(appId, moId, moMessage);
            }

            Job job = appService.getJobForApplicationForMO(appId, moId);
            String moduleName = job == null ? null : job.getModuleName();
            String content = ApplicationNotificationUtil.buildStatusUpdateContent(
                    app.getStatus(), moduleName, feedback, moMessage);
            notificationService.createNotification(
                    app.getStudentId(), "STATUS_UPDATE", content, app.getApplicationId());

            session.setAttribute("moActionSuccess", "Application review saved and TA notification sent.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("moActionError", "Failed to update application: " + e.getMessage());
        }

        String redirect = trimToNull(req.getParameter("redirect"));
        if ("detail".equalsIgnoreCase(redirect)) {
            resp.sendRedirect(req.getContextPath() + "/mo/view/application?applicationId=" + appId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
        }
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.NotificationService;

/**
 * MO: Approve / reject / mark interview for a TA application and send notification.
 */
@WebServlet("/mo/application/action")
public class MOApplicationActionServlet extends HttpServlet {
    private final ApplicationService appService = new ApplicationService();
    private final NotificationService notiService = new NotificationService();

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

        String moId       = (String) session.getAttribute("userAccount");
        String appId      = req.getParameter("applicationId");
        String newStatus  = req.getParameter("status");       // APPROVED / REJECTED / INTERVIEW
        String feedback   = req.getParameter("feedback");
        String taId       = req.getParameter("taId");
        String moduleName = req.getParameter("moduleName");

        if (appId == null || appId.trim().isEmpty() || newStatus == null) {
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
            return;
        }

        try {
            appService.updateApplicationStatus(appId.trim(), newStatus.trim(), feedback, moId);

            // Send notification to the TA
            if (taId != null && !taId.trim().isEmpty()) {
                String module = moduleName != null ? moduleName : "a position";
                String content;
                switch (newStatus.trim().toUpperCase()) {
                    case "APPROVED":
                        content = "Congratulations! Your application for " + module + " has been approved.";
                        break;
                    case "REJECTED":
                        content = "Your application for " + module + " has not been successful at this time.";
                        break;
                    case "INTERVIEW":
                        content = "You have been invited to an interview for " + module + ". Please check your email for details.";
                        break;
                    default:
                        content = "Your application status for " + module + " has been updated to " + newStatus + ".";
                }
                if (feedback != null && !feedback.trim().isEmpty()) {
                    content += " Feedback: " + feedback.trim();
                }
                notiService.createNotification(taId.trim(), "STATUS_UPDATE", content, appId.trim());
            }

            req.getSession().setAttribute("moActionSuccess", "Application " + newStatus.toLowerCase() + " successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("moActionError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/mo/applications");
    }
}

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
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.NotificationService;
import com.bupt.tarecruit.util.ApplicationNotificationUtil;

@WebServlet("/mo/view/application")
public class MoViewApplicationServlet extends HttpServlet {
    private final ApplicationService appService = new ApplicationService();
    private final NotificationService notificationService = new NotificationService();
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            response.sendError(403, "Access denied: MO role required.");
            return;
        }

        String appId = trimToNull(request.getParameter("applicationId"));
        if (appId == null) {
            session.setAttribute("moActionError", "Application ID is required.");
            response.sendRedirect(request.getContextPath() + "/mo/applications");
            return;
        }

        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");

        try {
            Application application = appService.getApplicationForMO(appId, moId);
            Job job = appService.getJobForApplicationForMO(appId, moId);
            User ta = userRepo.getUserById(application.getStudentId());

            request.setAttribute("userId", moId);
            request.setAttribute("userName", moName);
            request.setAttribute("application", application);
            request.setAttribute("job", job);
            request.setAttribute("taUser", ta);

            request.getRequestDispatcher("/jsp/mo/mo_viewapplication.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("moActionError", "Failed to load application: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/applications");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            response.sendError(403, "Access denied: MO role required.");
            return;
        }

        String appId = trimToNull(request.getParameter("applicationId"));
        if (appId == null) {
            session.setAttribute("moActionError", "Application ID is required.");
            response.sendRedirect(request.getContextPath() + "/mo/applications");
            return;
        }

        String moId = (String) session.getAttribute("userAccount");
        try {
            String status = request.getParameter("status");
            String applicationType = request.getParameter("applicationType");
            String feedback = request.getParameter("feedback");
            String moMessage = request.getParameter("moMessage");

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

        response.sendRedirect(request.getContextPath() + "/mo/view/application?applicationId=" + appId);
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

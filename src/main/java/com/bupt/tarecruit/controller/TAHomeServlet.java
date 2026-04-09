package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Notification;
import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.NotificationService;
import com.bupt.tarecruit.service.TAProfileService;

@WebServlet("/ta/home")
public class TAHomeServlet extends HttpServlet {
    private final TAProfileService profileService = new TAProfileService();
    private final ApplicationService applicationService = new ApplicationService();
    private final UserRepository userRepository = new UserRepository();
    private final NotificationService notificationService = new NotificationService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) req.getSession().getAttribute("userRole");
        if (role != null && !"TA".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: TA role required.");
            return;
        }

        try {
            moveFlash(req, "profileErrorMsg");
            moveFlash(req, "cvSuccess");
            TAProfile profile = profileService.getProfile(studentId);
            List<ApplicationView> applications = applicationService.getTAApplicationList(studentId);
            List<Notification> notifications = notificationService.getNotificationsForTa(studentId);
            User currentUser = userRepository.getUserById(studentId);

            req.setAttribute("studentId", studentId);
            req.setAttribute("profile", profile);
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("unreadCount", notificationService.getUnreadCount(studentId));
            req.setAttribute("applicationList", applications);
            req.setAttribute("applicationListJson", gson.toJson(applications));
            req.setAttribute("notifications", notifications);
            req.getRequestDispatcher("/jsp/ta/home.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Failed to load TA home", e);
        }
    }

    private void moveFlash(HttpServletRequest req, String key) {
        Object value = req.getSession().getAttribute(key);
        if (value != null) {
            req.setAttribute(key, value);
            req.getSession().removeAttribute(key);
        }
    }
}

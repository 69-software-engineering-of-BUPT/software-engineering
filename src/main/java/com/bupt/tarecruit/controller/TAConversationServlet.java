package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Map;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.NotificationService;
import com.bupt.tarecruit.service.TAConversationReadService;

/**
 * Standalone TA conversation page (figure 2 layout, no back button).
 */
@WebServlet("/ta/conversations")
public class TAConversationServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
    private final ApplicationService applicationService = new ApplicationService();
    private final NotificationService notificationService = new NotificationService();
    private final TAConversationReadService readService = new TAConversationReadService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String studentId = session == null ? null : (String) session.getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (role != null && !"TA".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied: TA role required.");
            return;
        }

        try {
            User currentUser = userRepo.getUserById(studentId);
            List<ApplicationView> applications = applicationService.getTAApplicationList(studentId);
            if (applications == null) applications = new ArrayList<ApplicationView>();
            int unreadCount = notificationService.getUnreadCount(studentId);

            String requested = req.getParameter("applicationId");
            String activeApplicationId = null;
            if (requested != null && !requested.trim().isEmpty()) {
                for (ApplicationView a : applications) {
                    if (requested.trim().equals(a.getApplicationId())) {
                        activeApplicationId = a.getApplicationId();
                        break;
                    }
                }
            }
            if (activeApplicationId == null && !applications.isEmpty()) {
                activeApplicationId = applications.get(0).getApplicationId();
            }

            // Auto-mark the active thread as read when TA explicitly navigates to it via ?applicationId=.
            if (activeApplicationId != null && requested != null && !requested.trim().isEmpty()) {
                for (ApplicationView a : applications) {
                    if (activeApplicationId.equals(a.getApplicationId())) {
                        try { readService.markLatestMoMessageRead(studentId, activeApplicationId, a.getStatement()); }
                        catch (Exception ignored) { }
                        break;
                    }
                }
            }

            Map<String, String> readKeys = readService.snapshotReadKeys(studentId);
            int conversationUnreadCount = 0;
            try { conversationUnreadCount = readService.countUnreadThreads(studentId); } catch (Exception ignored) { }

            req.setAttribute("studentId", studentId);
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("unreadCount", unreadCount);
            req.setAttribute("conversationUnreadCount", conversationUnreadCount);
            req.setAttribute("applicationList", applications);
            req.setAttribute("activeApplicationId", activeApplicationId);
            req.setAttribute("conversationReadKeys", readKeys);
            req.getRequestDispatcher("/jsp/ta/conversations.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load TA conversations");
        }
    }
}

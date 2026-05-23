package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.TAConversationReadService;

/**
 * AJAX endpoint used by the TA conversation page to mark the most recent MO
 * message of a given application as read.
 */
@WebServlet("/ta/conversations/read")
public class TAConversationReadServlet extends HttpServlet {
    private final TAConversationReadService readService = new TAConversationReadService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String taId = session == null ? null : (String) session.getAttribute("userAccount");
        if (taId == null) { sendJson(resp, 401, "{\"error\":\"Login required\"}"); return; }
        String role = (String) session.getAttribute("userRole");
        if (role != null && !"TA".equalsIgnoreCase(role)) {
            sendJson(resp, 403, "{\"error\":\"TA role required\"}");
            return;
        }
        String appId = req.getParameter("applicationId");
        if (appId == null || appId.trim().isEmpty()) {
            sendJson(resp, 400, "{\"error\":\"Application ID is required\"}");
            return;
        }
        String trimmed = appId.trim();
        try {
            List<ApplicationView> apps = applicationService.getTAApplicationList(taId);
            String statement = null;
            if (apps != null) {
                for (ApplicationView a : apps) {
                    if (trimmed.equals(a.getApplicationId())) {
                        statement = a.getStatement();
                        break;
                    }
                }
            }
            if (statement == null) {
                sendJson(resp, 404, "{\"error\":\"Application not found\"}");
                return;
            }
            readService.markLatestMoMessageRead(taId, trimmed, statement);
            int unread = readService.countUnreadThreads(taId);
            sendJson(resp, 200, "{\"success\":true,\"unreadCount\":" + unread + "}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(resp, 500, "{\"error\":\"Failed to mark conversation read\"}");
        }
    }

    private void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}

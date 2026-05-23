package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.MOConversationService;

@WebServlet("/mo/conversations/read")
public class MOConversationReadServlet extends HttpServlet {
    private final MOConversationService conversationService = new MOConversationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            sendJson(resp, 401, "{\"error\":\"Login required\"}");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            sendJson(resp, 403, "{\"error\":\"MO role required\"}");
            return;
        }

        String moId = (String) session.getAttribute("userAccount");
        String appId = req.getParameter("applicationId");
        if (appId == null || appId.trim().isEmpty()) {
            sendJson(resp, 400, "{\"error\":\"Application ID is required\"}");
            return;
        }

        try {
            conversationService.markThreadRead(moId, appId.trim());
            int unreadCount = conversationService.countUnreadThreads(moId);
            sendJson(resp, 200, "{\"success\":true,\"unreadCount\":" + unreadCount + "}");
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

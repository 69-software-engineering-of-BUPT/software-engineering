package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.ConversationThread;
import com.bupt.tarecruit.service.MOConversationService;

@WebServlet("/mo/conversations")
public class MOConversationServlet extends HttpServlet {
    private final MOConversationService conversationService = new MOConversationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        String moName = (String) session.getAttribute("userName");

        try {
            List<ConversationThread> threads = conversationService.getThreadsForMO(moId);
            String activeApplicationId = resolveActiveApplicationId(req.getParameter("applicationId"), threads);
            if (activeApplicationId != null) {
                for (ConversationThread thread : threads) {
                    if (activeApplicationId.equals(thread.getApplicationId()) && thread.isNeedsMoReply()) {
                        conversationService.markThreadRead(moId, activeApplicationId);
                        threads = conversationService.getThreadsForMO(moId);
                        break;
                    }
                }
            }

            int unreadCount = 0;
            for (ConversationThread thread : threads) {
                if (thread.isNeedsMoReply()) unreadCount++;
            }

            req.setAttribute("userId", moId);
            req.setAttribute("userName", moName);
            req.setAttribute("activeApplicationId", activeApplicationId);
            req.setAttribute("conversationThreads", threads);
            req.setAttribute("conversationUnreadCount", unreadCount);
            req.getRequestDispatcher("/jsp/mo/conversations.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("moActionError", "Failed to load conversations: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/mo/home");
        }
    }

    private String resolveActiveApplicationId(String requestedId, List<ConversationThread> threads) {
        if (threads == null || threads.isEmpty()) return null;
        if (requestedId != null && !requestedId.trim().isEmpty()) {
            String trimmed = requestedId.trim();
            for (ConversationThread thread : threads) {
                if (trimmed.equals(thread.getApplicationId())) {
                    return trimmed;
                }
            }
        }
        return threads.get(0).getApplicationId();
    }
}

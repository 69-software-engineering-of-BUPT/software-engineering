package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.ConversationMessage;
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

        // JSON API for the MO conversation widget
        if ("json".equalsIgnoreCase(req.getParameter("format"))) {
            try {
                List<ConversationThread> threads = conversationService.getThreadsForMO(moId);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.setHeader("Cache-Control", "no-store");
                resp.getWriter().write(threadsToJson(threads));
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"Failed to load conversations\"}");
            }
            return;
        }

        try {
            List<ConversationThread> threads = conversationService.getThreadsForMO(moId);
            String requestedApplicationId = req.getParameter("applicationId");
            String activeApplicationId = resolveActiveApplicationId(requestedApplicationId, threads);
            // Only auto-mark-read when MO explicitly navigates to a specific thread via URL param
            if (activeApplicationId != null && requestedApplicationId != null && !requestedApplicationId.trim().isEmpty()) {
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

    private String threadsToJson(List<ConversationThread> threads) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < threads.size(); i++) {
            if (i > 0) sb.append(",");
            ConversationThread t = threads.get(i);
            sb.append("{");
            sb.append("\"applicationId\":").append(jsonStr(t.getApplicationId())).append(",");
            sb.append("\"jobId\":").append(jsonStr(t.getJobId())).append(",");
            sb.append("\"moduleName\":").append(jsonStr(t.getModuleName())).append(",");
            sb.append("\"taId\":").append(jsonStr(t.getTaId())).append(",");
            sb.append("\"status\":").append(jsonStr(t.getStatus())).append(",");
            sb.append("\"applicationType\":").append(jsonStr(t.getApplicationType())).append(",");
            sb.append("\"applyTime\":").append(jsonStr(t.getApplyTime())).append(",");
            sb.append("\"needsMoReply\":").append(t.isNeedsMoReply()).append(",");
            sb.append("\"messages\":[");
            List<ConversationMessage> msgs = t.getMessages();
            for (int j = 0; j < msgs.size(); j++) {
                if (j > 0) sb.append(",");
                ConversationMessage m = msgs.get(j);
                sb.append("{");
                sb.append("\"sender\":").append(jsonStr(m.getSender())).append(",");
                sb.append("\"sentAt\":").append(jsonStr(m.getSentAt())).append(",");
                sb.append("\"content\":").append(jsonStr(m.getContent()));
                sb.append("}");
            }
            sb.append("]");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String jsonStr(String value) {
        if (value == null) return "null";
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n") + "\"";
    }
}

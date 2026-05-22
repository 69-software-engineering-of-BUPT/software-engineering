package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.ApplicationService;

/**
 * MO sends a chat message in an application's conversation thread.
 * Appends "[timestamp MO]: message" to the application's statement field.
 */
@WebServlet("/mo/application/reply")
public class MOApplicationReplyServlet extends HttpServlet {

    private final ApplicationService appService = new ApplicationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            if (isAjax(req)) {
                sendJson(resp, 401, "{\"error\":\"Login required\"}");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            if (isAjax(req)) {
                sendJson(resp, 403, "{\"error\":\"MO role required\"}");
                return;
            }
            resp.sendError(403, "Access denied: MO role required.");
            return;
        }

        String moId      = (String) session.getAttribute("userAccount");
        String appId     = req.getParameter("applicationId");
        String message   = req.getParameter("message");
        String redirect  = req.getParameter("redirect");

        if (appId == null || appId.trim().isEmpty()) {
            if (isAjax(req)) {
                sendJson(resp, 400, "{\"error\":\"Application ID is required\"}");
                return;
            }
            resp.sendRedirect(resolveRedirect(req, redirect));
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            if (isAjax(req)) {
                sendJson(resp, 400, "{\"error\":\"Message is required\"}");
                return;
            }
            resp.sendRedirect(resolveRedirect(req, redirect));
            return;
        }

        try {
            appService.appendMOMessage(appId.trim(), moId, message.trim());
            if (isAjax(req)) {
                String sentAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                sendJson(resp, 200, "{\"success\":true,\"sentAt\":\"" + jsonEscape(sentAt) + "\"}");
                return;
            }
            req.getSession().setAttribute("moActionSuccess", "Message sent.");
        } catch (Exception e) {
            e.printStackTrace();
            if (isAjax(req)) {
                sendJson(resp, 500, "{\"error\":\"" + jsonEscape("Failed to send message: " + e.getMessage()) + "\"}");
                return;
            }
            req.getSession().setAttribute("moActionError", "Failed to send message: " + e.getMessage());
        }

        resp.sendRedirect(resolveRedirect(req, redirect));
    }

    private String resolveRedirect(HttpServletRequest req, String redirect) {
        if ("conversations".equalsIgnoreCase(redirect)) {
            return req.getContextPath() + "/mo/conversations";
        }
        return req.getContextPath() + "/mo/applications";
    }

    private boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    private void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(json);
    }

    private String jsonEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}

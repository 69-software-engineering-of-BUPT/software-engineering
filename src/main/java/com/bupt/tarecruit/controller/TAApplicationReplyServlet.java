package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.service.ApplicationService;

/**
 * TA follow-up message in the application feedback thread (appends to stored statement via {@link ApplicationService#updateStatementFromChat}).
 */
@WebServlet("/ta/application/reply")
public class TAApplicationReplyServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String applicationId = req.getParameter("applicationId");
        String message = req.getParameter("message");
        if (applicationId == null || applicationId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/ta/home");
            return;
        }

        try {
            Application app = applicationRepository.findById(applicationId.trim());
            if (app == null || !studentId.equals(app.getStudentId())) {
                if (isAjax(req)) {
                    sendJson(resp, 403, "{\"error\":\"Cannot update this application\"}");
                } else {
                    resp.sendError(403, "Cannot update this application");
                }
                return;
            }
            if (message != null && !message.trim().isEmpty()) {
                applicationService.updateStatementFromChat(applicationId.trim(), message.trim());
            }
            if (isAjax(req)) {
                sendJson(resp, 200, "{\"success\":true}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/ta/home");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isAjax(req)) {
                sendJson(resp, 500, "{\"error\":\"Failed to send reply\"}");
            } else {
                resp.sendError(500, "Failed to send reply");
            }
        }
    }

    private boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    private void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}

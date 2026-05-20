package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.AdminService;

public class ADLogRecordServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            resp.sendError(401, "Login required.");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied: ADMIN role required.");
            return;
        }

        String actionType = req.getParameter("actionType");
        String targetType = req.getParameter("targetType");
        String targetId = req.getParameter("targetId");
        String targetName = req.getParameter("targetName");
        String result = req.getParameter("result");
        String message = req.getParameter("message");

        adminService.recordOperationLog(
            (String) session.getAttribute("userAccount"),
            (String) session.getAttribute("userName"),
            role,
            actionType,
            targetType,
            targetId,
            targetName,
            result,
            message
        );

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"success\":true}");
    }
}

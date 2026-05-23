package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.OperationLog;
import com.bupt.tarecruit.service.AdminService;
import com.google.gson.Gson;

public class ADLogServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied: ADMIN role required.");
            return;
        }

        List<OperationLog> logs = adminService.getOperationLogs();
        req.setAttribute("operationLogs", logs);
        req.setAttribute("operationLogsJson", gson.toJson(logs));
        req.getRequestDispatcher("/jsp/ad/logs.jsp").forward(req, resp);
    }
}

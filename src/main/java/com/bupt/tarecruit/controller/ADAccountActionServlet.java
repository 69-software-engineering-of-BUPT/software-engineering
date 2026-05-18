package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.AdminService;

@WebServlet("/ad/accounts/action")
public class ADAccountActionServlet extends HttpServlet {
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

        String userId = req.getParameter("userId");
        String action = req.getParameter("action");
        String actorId = (String) session.getAttribute("userAccount");
        String actorName = (String) session.getAttribute("userName");

        try {
            if ("freeze".equalsIgnoreCase(action)) {
                adminService.freezeAccount(userId, actorId, actorName, role);
            } else if ("unfreeze".equalsIgnoreCase(action)) {
                adminService.unfreezeAccount(userId, actorId, actorName, role);
            } else if ("delete".equalsIgnoreCase(action)) {
                adminService.deleteAccount(userId, actorId, actorName, role);
            } else {
                resp.sendError(400, "Unknown account action.");
                return;
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"success\":true}");
    }
}

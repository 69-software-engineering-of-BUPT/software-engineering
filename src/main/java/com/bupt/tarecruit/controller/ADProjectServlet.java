package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.AdminService;
import com.google.gson.Gson;

@WebServlet("/ad/projects")
public class ADProjectServlet extends HttpServlet {
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

        try {
            req.setAttribute("projectViewsJson", gson.toJson(adminService.getProjectViews()));
        } catch (Exception e) {
            req.setAttribute("projectViewsJson", "[]");
        }

        req.getRequestDispatcher("/jsp/ad/projects.jsp").forward(req, resp);
    }
}

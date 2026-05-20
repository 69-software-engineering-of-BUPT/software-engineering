package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Entry point for ADMIN role after login. Redirects to the accounts management page.
 */
@WebServlet("/ad/home")
public class ADHomeServlet extends HttpServlet {

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
        resp.sendRedirect(req.getContextPath() + "/ad/accounts");
    }
}

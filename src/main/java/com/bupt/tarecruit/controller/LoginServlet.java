package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.AuthService;
import com.bupt.tarecruit.service.AuthenticatedUser;
import com.bupt.tarecruit.service.AuthenticationException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userAccount") != null) {
            try {
                resp.sendRedirect(req.getContextPath() + targetFor((String) session.getAttribute("userRole")));
            } catch (AuthenticationException ex) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String userId = req.getParameter("userId");
        try {
            AuthenticatedUser user = authService.authenticate(userId, req.getParameter("password"));
            HttpSession session = req.getSession(true);
            session.setAttribute("userAccount", user.getUserId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userName", user.getName());
            resp.sendRedirect(req.getContextPath() + targetFor(user.getRole()));
        } catch (AuthenticationException ex) {
            String inputUserId = userId == null ? "" : userId.trim();
            req.setAttribute("loginError", ex.getMessage());
            req.setAttribute("loginUserId", inputUserId);
            req.setAttribute("inputUserId", inputUserId);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Login failed", ex);
        }
    }

    private String targetFor(String role) {
        if ("TA".equals(role)) {
            return "/ta/home";
        }
        if ("MO".equals(role)) {
            return "/mo/home";
        }
        if ("ADMIN".equals(role)) {
            return "/ad/accounts";
        }
        throw new AuthenticationException("Unsupported user role.");
    }
}

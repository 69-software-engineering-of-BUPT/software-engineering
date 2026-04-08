package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();

    /** Show the login page. If already signed in, redirect straight to the role home. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userAccount") != null) {
            redirectByRole((String) session.getAttribute("userRole"), req, resp);
            return;
        }
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    /** Process login form submission. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String userId   = req.getParameter("userId");
        String password = req.getParameter("password");

        if (userId == null || userId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("loginError", "Please enter both User ID and password.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            return;
        }

        userId = userId.trim();
        try {
            User user = userRepo.getUserById(userId);
            if (user == null || !password.equals(user.getPassword())) {
                req.setAttribute("loginError", "Invalid User ID or password.");
                req.setAttribute("inputUserId", userId);
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                return;
            }

            // Credentials valid — create session
            HttpSession session = req.getSession(true);
            session.setAttribute("userAccount", user.getUserId());
            session.setAttribute("userRole",    user.getRole());
            session.setAttribute("userName",    user.getName());

            redirectByRole(user.getRole(), req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("loginError", "System error. Please try again.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }

    private void redirectByRole(String role, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        switch (role.toUpperCase()) {
            case "TA":
                resp.sendRedirect(req.getContextPath() + "/ta/home");
                break;
            case "MO":
                resp.sendRedirect(req.getContextPath() + "/mo/home");
                break;
            case "ADMIN":
                resp.sendRedirect(req.getContextPath() + "/ad/home");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}

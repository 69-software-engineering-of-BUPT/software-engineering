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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userAccount") != null) {
            String role = (String) session.getAttribute("userRole");
            if ("ADMIN".equalsIgnoreCase(role)) {
                resp.sendRedirect(req.getContextPath() + "/ad/home");
            } else if ("MO".equalsIgnoreCase(role)) {
                resp.sendRedirect(req.getContextPath() + "/mo/home");
            } else {
                resp.sendRedirect(req.getContextPath() + "/ta/home");
            }
            return;
        }
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId   = req.getParameter("userId");
        String name     = req.getParameter("name");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirmPassword");

        if (userId != null) userId = userId.trim();
        if (name  != null) name  = name.trim();

        // Basic validation
        if (isBlank(userId) || isBlank(name) || isBlank(password) || isBlank(confirm)) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (!password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("prevUserId", userId);
            req.setAttribute("prevName", name);
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.setAttribute("prevUserId", userId);
            req.setAttribute("prevName", name);
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        // Check uniqueness
        User existing = null;
        try {
            existing = userRepo.getUserById(userId);
        } catch (Exception ignored) { }
        if (existing != null) {
            req.setAttribute("error", "User ID \"" + userId + "\" is already taken. Please choose another.");
            req.setAttribute("prevName", name);
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        // Create TA account
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setPassword(password);
        newUser.setRole("TA");
        newUser.setName(name);
        newUser.setActiveJobsCount(0);
        try {
            userRepo.saveUser(newUser);
        } catch (IOException e) {
            req.setAttribute("error", "Failed to create account. Please try again.");
            req.setAttribute("prevUserId", userId);
            req.setAttribute("prevName", name);
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("success", "Account created! You can now sign in with your new TA account.");
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

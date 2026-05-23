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
import com.bupt.tarecruit.service.AuthenticationException;
import com.bupt.tarecruit.util.RoleHomeUtil;

/**
 * Handles self-service TA account registration.
 * The servlet validates input, rejects duplicate user IDs, and persists a new
 * {@code TA} account through the user repository.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserRepository userRepo;

    /**
     * Creates a servlet backed by the default user repository.
     */
    public RegisterServlet() {
        this(new UserRepository());
    }

    /**
     * Creates a servlet with an injected repository for persistence and tests.
     *
     * @param userRepo repository used to look up and save accounts
     */
    RegisterServlet(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Shows the registration page for anonymous visitors.
     * Logged-in users are redirected to the home page that matches their role,
     * while invalid session roles are cleared and sent back to sign-in.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userAccount") != null) {
            try {
                resp.sendRedirect(req.getContextPath()
                        + RoleHomeUtil.resolveHomePath((String) session.getAttribute("userRole")));
            } catch (AuthenticationException ex) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    /**
     * Validates the submitted registration form and creates a new TA account.
     * Validation failures are returned to the same page so the user can fix the
     * input without losing the entered ID and name.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
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
        if (!isValidUserId(userId)) {
            req.setAttribute("error", "User ID may contain only letters, numbers, and underscores.");
            req.setAttribute("prevUserId", userId);
            req.setAttribute("prevName", name);
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
            req.setAttribute("prevUserId", userId);
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

    /**
     * Checks whether a submitted value is null or contains only whitespace.
     *
     * @param s submitted text value
     * @return {@code true} when the value is blank
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Restricts user IDs to letters, digits, and underscores.
     *
     * @param userId candidate user ID from the form
     * @return {@code true} when the ID matches the accepted format
     */
    private boolean isValidUserId(String userId) {
        return userId != null && userId.matches("[A-Za-z0-9_]+");
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.google.gson.Gson;

@WebServlet("/ad/accounts")
public class AdminServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
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
        req.setAttribute("userId",   session.getAttribute("userAccount"));
        req.setAttribute("userName", session.getAttribute("userName"));

        // Load all users for account list; also keep taUsers subset for CV section
        try {
            List<User> allUsers = userRepo.getAllUsers();
            List<User> taUsers = new ArrayList<>();
            for (User u : allUsers) {
                if ("TA".equalsIgnoreCase(u.getRole())) taUsers.add(u);
            }
            req.setAttribute("allUsersJson", gson.toJson(allUsers));
            req.setAttribute("taUsersJson",  gson.toJson(taUsers));
        } catch (Exception e) {
            req.setAttribute("allUsersJson", "[]");
            req.setAttribute("taUsersJson",  "[]");
        }

        req.getRequestDispatcher("/jsp/ad/accounts.jsp").forward(req, resp);
    }
}

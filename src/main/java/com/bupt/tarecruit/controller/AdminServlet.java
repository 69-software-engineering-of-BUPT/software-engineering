package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            List<Map<String, Object>> accountViews = new ArrayList<>();
            for (User u : allUsers) {
                if ("TA".equalsIgnoreCase(u.getRole())) taUsers.add(u);

                Map<String, Object> view = new LinkedHashMap<>();
                view.put("userId", u.getUserId());
                view.put("role", u.getRole());
                view.put("name", u.getName());
                view.put("cvFilePath", u.getCvFilePath());
                view.put("activeJobsCount", u.getActiveJobsCount());

                String studentId = u.getUserId();
                String fullName = u.getName();
                String email = (u.getEmail() == null || u.getEmail().trim().isEmpty()) ? u.getUserId() : u.getEmail();
                String phoneNumber = (u.getPhoneNumber() == null || u.getPhoneNumber().trim().isEmpty()) ? "—" : u.getPhoneNumber();
                String researchArea = (u.getResearchArea() == null || u.getResearchArea().trim().isEmpty()) ? "—" : u.getResearchArea();
                String cet6Grade = (u.getCet6Grade() == null || u.getCet6Grade().trim().isEmpty()) ? "—" : u.getCet6Grade();

                view.put("studentId", studentId);
                view.put("fullName", fullName);
                view.put("email", email);
                view.put("phoneNumber", phoneNumber);
                view.put("researchArea", researchArea);
                view.put("cet6Grade", cet6Grade);
                accountViews.add(view);
            }
            req.setAttribute("allUsersJson", gson.toJson(accountViews));
            req.setAttribute("taUsersJson",  gson.toJson(taUsers));
        } catch (IOException e) {
            req.setAttribute("allUsersJson", "[]");
            req.setAttribute("taUsersJson",  "[]");
        }

        req.getRequestDispatcher("/jsp/ad/accounts.jsp").forward(req, resp);
    }
}

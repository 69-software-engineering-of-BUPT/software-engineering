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

import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.TAProfileRepository;
import com.bupt.tarecruit.repository.UserRepository;
import com.google.gson.Gson;

@WebServlet("/ad/accounts")
public class AdminServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();
    private final TAProfileRepository taProfileRepo = new TAProfileRepository();
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
                view.put("major", u.getMajor());
                view.put("cvFilePath", u.getCvFilePath());
                view.put("activeJobsCount", u.getActiveJobsCount());

                TAProfile profile = null;
                if ("TA".equalsIgnoreCase(u.getRole()) && u.getUserId() != null) {
                    try {
                        profile = taProfileRepo.findById(u.getUserId());
                    } catch (Exception ignored) {
                        profile = null;
                    }
                }

                String studentId = u.getUserId();
                String fullName = u.getName();
                String email = u.getUserId();
                String phoneNumber = "—";
                String researchArea = (u.getMajor() == null || u.getMajor().trim().isEmpty()) ? "—" : u.getMajor();
                String cet6Grade = "—";

                if (profile != null) {
                    studentId = (profile.getStudentId() == null || profile.getStudentId().trim().isEmpty()) ? studentId : profile.getStudentId();
                    fullName = (profile.getFullName() == null || profile.getFullName().trim().isEmpty()) ? fullName : profile.getFullName();
                    email = (profile.getEmail() == null || profile.getEmail().trim().isEmpty()) ? email : profile.getEmail();
                    phoneNumber = (profile.getPhoneNumber() == null || profile.getPhoneNumber().trim().isEmpty()) ? "—" : profile.getPhoneNumber();
                    researchArea = (profile.getResearchArea() == null || profile.getResearchArea().trim().isEmpty()) ? researchArea : profile.getResearchArea();
                    cet6Grade = (profile.getCet6Grade() == null || profile.getCet6Grade().trim().isEmpty()) ? "—" : profile.getCet6Grade();
                }

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
        } catch (Exception e) {
            req.setAttribute("allUsersJson", "[]");
            req.setAttribute("taUsersJson",  "[]");
        }

        req.getRequestDispatcher("/jsp/ad/accounts.jsp").forward(req, resp);
    }
}

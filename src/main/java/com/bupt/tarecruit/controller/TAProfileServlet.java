package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.service.TAProfileService;

@WebServlet("/ta/profile")
public class TAProfileServlet extends HttpServlet {
    final private TAProfileService service = new TAProfileService();

    /**
     * Load existing profile data to the form
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String loggedInId = (String) session.getAttribute("userAccount");

        if (loggedInId == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/ta/home");
    }

    /**
     * Handle the form submission for updating profile
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        // Securely retrieve student ID from the session (Login Account = Student ID)
        String loggedInId = (String) req.getSession().getAttribute("userAccount");

        if (loggedInId == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }

        try {
            TAProfile profile = new TAProfile();
            // Bind the profile to the current session user
            profile.setStudentId(loggedInId); 
            
            // Map parameters from the request form
            profile.setFullName(req.getParameter("fullName"));
            profile.setEmail(req.getParameter("email"));
            profile.setPhoneNumber(req.getParameter("phoneNumber"));
            profile.setResearchArea(req.getParameter("researchArea"));
            profile.setCet6Grade(req.getParameter("cet6Grade"));

            // Call service to validate and save
            service.updateProfile(profile);

            resp.sendRedirect(req.getContextPath() + "/ta/home");

        } catch (Exception e) {
            req.getSession().setAttribute("profileErrorMsg", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        }
    }
}
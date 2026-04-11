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

@WebServlet("/ta/profile")
public class TAProfileServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("userAccount") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
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
        String loggedInId = (String) req.getSession().getAttribute("userAccount");
        if (loggedInId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            String fullName = req.getParameter("fullName");
            String email    = req.getParameter("email");
            String phone    = req.getParameter("phoneNumber");
            String research = req.getParameter("researchArea");
            String cet6     = req.getParameter("cet6Grade");

            if (isEmpty(fullName))  throw new RuntimeException("Full Name is mandatory");
            if (isEmpty(email))     throw new RuntimeException("Email is mandatory");
            if (isEmpty(phone))     throw new RuntimeException("Phone Number is mandatory");
            if (isEmpty(research))  throw new RuntimeException("Research Area is mandatory");
            if (isEmpty(cet6))      throw new RuntimeException("CET6 Grade is mandatory");

            User user = userRepo.getUserById(loggedInId);
            if (user == null) throw new RuntimeException("User not found");

            user.setName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setResearchArea(research);
            user.setCet6Grade(cet6);

            userRepo.saveUser(user);
            req.getSession().setAttribute("userName", fullName);

            resp.sendRedirect(req.getContextPath() + "/ta/home");
        } catch (Exception e) {
            req.getSession().setAttribute("profileErrorMsg", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
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
        resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            AuthenticatedUser user = authService.authenticate(req.getParameter("userId"), req.getParameter("password"));
            HttpSession session = req.getSession(true);
            session.setAttribute("userAccount", user.getUserId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userName", user.getName());
            resp.sendRedirect(req.getContextPath() + targetFor(user.getRole()));
        } catch (AuthenticationException ex) {
            req.setAttribute("loginError", ex.getMessage());
            req.setAttribute("loginUserId", req.getParameter("userId"));
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
        return "/ad/accounts";
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prototype TA entry: sets session {@code userAccount} and redirects to the TA home page.
 */
@WebServlet("/ta/enter")
public class TAEnterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        if (userId == null || userId.trim().isEmpty()) {
            userId = "TA001";
        }
        req.getSession().setAttribute("userAccount", userId.trim());
        resp.sendRedirect(req.getContextPath() + "/ta/home");
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/myApplications")
public class TAApplicationListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve student ID from session
        String studentId = (String) req.getSession().getAttribute("userAccount");

        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Application history is rendered on the TA home page
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load application history");
        }
    }  
}
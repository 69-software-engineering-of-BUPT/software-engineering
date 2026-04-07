package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.service.ApplicationService;

@WebServlet("/ta/myApplications")
public class TAApplicationListServlet extends HttpServlet {
    final private ApplicationService service = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve student ID from session
        String studentId = (String) req.getSession().getAttribute("userAccount");

        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }

        try {
            // Get the list for the specific TA
            List<ApplicationView> list = service.getTAApplicationList(studentId);
            
            // Pass the list to JSP
            req.setAttribute("applicationList", list);
            
            // Forward to the status view page
            req.getRequestDispatcher("/jsp/my_applications.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load application history");
        }
    }  
}
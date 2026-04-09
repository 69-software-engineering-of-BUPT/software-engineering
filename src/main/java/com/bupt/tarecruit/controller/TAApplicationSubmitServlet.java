package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.ApplicationException;
import com.bupt.tarecruit.service.ApplicationService;

@WebServlet("/ta/applications")
public class TAApplicationSubmitServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        Application app = new Application();
        app.setStudentId(studentId);
        app.setJobId(req.getParameter("jobId"));
        app.setStatement(req.getParameter("statement"));
        try {
            applicationService.submitApplication(app);
            req.getSession().setAttribute("jobSuccessMsg", "Application submitted successfully.");
        } catch (ApplicationException ex) {
            req.getSession().setAttribute("jobErrorMsg", ex.getMessage());
        } catch (Exception ex) {
            throw new ServletException("Failed to submit application", ex);
        }
        resp.sendRedirect(req.getContextPath() + "/ta/jobs");
    }
}

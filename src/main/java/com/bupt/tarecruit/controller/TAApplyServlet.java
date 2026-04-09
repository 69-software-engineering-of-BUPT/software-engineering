package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;

/**
 * TA003 + TA005: Submit a new application (with optional statement).
 */
@WebServlet("/ta/apply")
public class TAApplyServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String jobId = req.getParameter("jobId");
        String statement = req.getParameter("statement");
        String applicationType = req.getParameter("applicationType");

        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
            return;
        }

        try {
            Job job = jobService.getJobById(jobId.trim());
            if (job == null || !"OPEN".equalsIgnoreCase(job.getStatus())) {
                req.getSession().setAttribute("applyError", "This position is no longer open for applications.");
                resp.sendRedirect(req.getContextPath() + "/ta/jobs");
                return;
            }

            // AD001: enforce 3-active-job limit
            User user = userRepo.getUserById(studentId);
            if (user != null && user.getActiveJobsCount() >= 3) {
                req.getSession().setAttribute("applyError", "You have reached the maximum of 3 active TA positions.");
                resp.sendRedirect(req.getContextPath() + "/ta/jobs");
                return;
            }

            Application app = new Application();
            app.setStudentId(studentId);
            app.setJobId(jobId.trim());
            app.setStatement(statement != null ? statement.trim() : "");
            app.setApplicationType(applicationType != null ? applicationType.trim() : "");
            app.setCvAttached("true".equals(req.getParameter("cvAttached")));

            applicationService.submitApplication(app);

            // Increment active jobs counter
            if (user != null) {
                user.setActiveJobsCount(user.getActiveJobsCount() + 1);
                userRepo.saveUser(user);
            }

            req.getSession().setAttribute("applySuccess", "Application submitted successfully for " + job.getModuleName() + "!");
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("applyError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
        }
    }
}

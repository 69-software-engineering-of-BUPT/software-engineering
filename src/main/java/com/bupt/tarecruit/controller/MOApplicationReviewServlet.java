package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;

@WebServlet("/mo/applications/review")
public class MOApplicationReviewServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String moId = (String) req.getSession().getAttribute("userAccount");
        String jobId = req.getParameter("jobId");
        try {
            Job job = jobService.findById(jobId);
            if (job == null || !moId.equals(job.getMoId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            applicationService.reviewApplication(req.getParameter("applicationId"), moId, req.getParameter("decision"), req.getParameter("feedback"));
        } catch (Exception ex) {
            req.getSession().setAttribute("reviewErrorMsg", ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/mo/applications?jobId=" + jobId);
    }
}

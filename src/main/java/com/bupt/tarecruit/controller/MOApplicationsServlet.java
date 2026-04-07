package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;

@WebServlet("/mo/applications")
public class MOApplicationsServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String moId = (String) req.getSession().getAttribute("userAccount");
        String jobId = req.getParameter("jobId");
        try {
            Job job = jobService.findById(jobId);
            if (job == null || !moId.equals(job.getMoId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            List<ApplicationView> applications = applicationService.getApplicationsForJob(jobId);
            req.setAttribute("job", job);
            req.setAttribute("applications", applications);
            Object error = req.getSession().getAttribute("reviewErrorMsg");
            if (error != null) {
                req.setAttribute("reviewErrorMsg", error);
                req.getSession().removeAttribute("reviewErrorMsg");
            }
            req.getRequestDispatcher("/jsp/mo/applications.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Failed to load applications", ex);
        }
    }
}

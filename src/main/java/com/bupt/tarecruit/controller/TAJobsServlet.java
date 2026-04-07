package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;

@WebServlet("/ta/jobs")
public class TAJobsServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentId = (String) req.getSession().getAttribute("userAccount");
        try {
            List<Job> jobs = jobService.getOpenJobs();
            Set<String> appliedJobIds = new HashSet<String>();
            for (ApplicationView view : applicationService.getTAApplicationList(studentId)) {
                appliedJobIds.add(view.getJobId());
            }
            req.setAttribute("jobs", jobs);
            req.setAttribute("appliedJobIds", appliedJobIds);
            moveFlash(req, "jobSuccessMsg");
            moveFlash(req, "jobErrorMsg");
            req.getRequestDispatcher("/jsp/ta/jobs.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Failed to load jobs", ex);
        }
    }

    private void moveFlash(HttpServletRequest req, String key) {
        Object value = req.getSession().getAttribute(key);
        if (value != null) {
            req.setAttribute(key, value);
            req.getSession().removeAttribute(key);
        }
    }
}

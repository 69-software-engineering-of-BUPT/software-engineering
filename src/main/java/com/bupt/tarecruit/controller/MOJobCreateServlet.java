package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.JobService;

@WebServlet("/mo/jobs")
public class MOJobCreateServlet extends HttpServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String moId = (String) req.getSession().getAttribute("userAccount");
        String moName = (String) req.getSession().getAttribute("userName");
        Job job = new Job();
        job.setMoId(moId);
        job.setMdName(moName);
        job.setModuleName(req.getParameter("moduleName"));
        job.setJobType(req.getParameter("jobType"));
        job.setRequirements(req.getParameter("requirements"));
        job.setIntroduction(req.getParameter("introduction"));
        try {
            job.setWeeklyWorkload(Integer.parseInt(req.getParameter("weeklyWorkload")));
        } catch (NumberFormatException ex) {
            job.setWeeklyWorkload(0);
        }
        job.setDeadline(req.getParameter("deadline"));
        job.setStatus("OPEN");
        try {
            jobService.createJob(job);
            req.getSession().setAttribute("moSuccessMsg", "Job posted successfully.");
        } catch (Exception ex) {
            req.getSession().setAttribute("moErrorMsg", "Failed to publish job: " + ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/mo/home");
    }
}

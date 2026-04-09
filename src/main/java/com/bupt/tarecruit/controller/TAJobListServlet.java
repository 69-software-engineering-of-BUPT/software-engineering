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
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
import com.google.gson.Gson;

/**
 * TA003: Browse open job positions.
 */
@WebServlet("/ta/jobs")
public class TAJobListServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final UserRepository userRepo = new UserRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Job> openJobs = jobService.getOpenJobs();
            Set<String> appliedJobIds = new HashSet<String>();
            for (ApplicationView view : applicationService.getTAApplicationList(studentId)) {
                appliedJobIds.add(view.getJobId());
            }

            req.setAttribute("jobList", openJobs);
            req.setAttribute("jobListJson", gson.toJson(openJobs));
            req.setAttribute("appliedJobIds", appliedJobIds);
            req.setAttribute("appliedJobIdsJson", gson.toJson(appliedJobIds));
            req.setAttribute("studentId", studentId);
            moveFlash(req, "applySuccess", "jobSuccessMsg");
            moveFlash(req, "applyError", "jobErrorMsg");

            try {
                User user = userRepo.getUserById(studentId);
                String cvPath = (user != null && user.getCvFilePath() != null) ? user.getCvFilePath() : "";
                req.setAttribute("cvFilePath", cvPath);
            } catch (Exception ignore) {
                req.setAttribute("cvFilePath", "");
            }

            req.getRequestDispatcher("/jsp/ta/jobs.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load job list");
        }
    }

    private void moveFlash(HttpServletRequest req, String primaryKey, String legacyKey) {
        Object value = req.getSession().getAttribute(primaryKey);
        if (value == null) {
            value = req.getSession().getAttribute(legacyKey);
        }
        if (value != null) {
            req.setAttribute(primaryKey, value);
        }
        req.getSession().removeAttribute(primaryKey);
        req.getSession().removeAttribute(legacyKey);
    }
}

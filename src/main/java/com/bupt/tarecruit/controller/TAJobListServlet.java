package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.JobService;

/**
 * TA003: Browse open job positions.
 */
@WebServlet("/ta/jobs")
public class TAJobListServlet extends HttpServlet {
    private final JobService jobService = new JobService();
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
            req.setAttribute("jobList", openJobs);
            req.setAttribute("jobListJson", gson.toJson(openJobs));
            req.setAttribute("studentId", studentId);

            // Pass current CV path so the apply modal can show it
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
}

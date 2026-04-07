package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.JobService;

@WebServlet("/mo/home")
public class MOHomeServlet extends HttpServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String moId = (String) req.getSession().getAttribute("userAccount");
        try {
            List<Job> jobs = jobService.getJobsByMoId(moId);
            req.setAttribute("jobs", jobs);
            moveFlash(req, "moSuccessMsg");
            moveFlash(req, "moErrorMsg");
            req.getRequestDispatcher("/jsp/mo/home.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Failed to load MO home", ex);
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

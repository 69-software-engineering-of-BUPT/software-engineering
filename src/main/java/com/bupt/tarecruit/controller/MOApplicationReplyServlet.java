package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.service.ApplicationService;

/**
 * MO sends a chat message in an application's conversation thread.
 * Appends "[timestamp MO]: message" to the application's statement field.
 */
@WebServlet("/mo/application/reply")
public class MOApplicationReplyServlet extends HttpServlet {

    private final ApplicationService appService = new ApplicationService();
    private final ApplicationRepository appRepo = new ApplicationRepository();
    private final JobRepository jobRepo = new JobRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            resp.sendError(403, "Access denied: MO role required.");
            return;
        }

        String moId      = (String) session.getAttribute("userAccount");
        String appId     = req.getParameter("applicationId");
        String message   = req.getParameter("message");

        if (appId == null || appId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
            return;
        }

        try {
            // Verify the application belongs to this MO's job
            Application app = appRepo.findById(appId.trim());
            if (app == null) {
                resp.sendError(404, "Application not found.");
                return;
            }
            Job job = jobRepo.findById(app.getJobId());
            if (job == null || !moId.equals(job.getMdId())) {
                resp.sendError(403, "You do not own this application's job position.");
                return;
            }

            if (message != null && !message.trim().isEmpty()) {
                appService.appendMOMessage(appId.trim(), moId, message.trim());
            }
            req.getSession().setAttribute("moActionSuccess", "Message sent.");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("moActionError", "Failed to send message: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/mo/applications");
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.service.ApplicationService;

/**
 * MO: View all applications submitted for this MO's job positions.
 */
@WebServlet("/mo/applications")
public class MOApplicationListServlet extends HttpServlet {
    private final ApplicationService appService = new ApplicationService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String moId = (String) session.getAttribute("userAccount");
        try {
            List<ApplicationView> apps = appService.getApplicationsForMO(moId);
            long pendingCount = apps.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();

            req.setAttribute("applications", apps);
            req.setAttribute("applicationsJson", gson.toJson(apps));
            req.setAttribute("moId", moId);
            req.setAttribute("moName", session.getAttribute("userName"));
            req.setAttribute("pendingCount", (int) pendingCount);
            req.getRequestDispatcher("/jsp/mo/applications.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load applications");
        }
    }
}

package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.service.ApplicationService;

@WebServlet("/mo/home")
public class MOHomeServlet extends HttpServlet {

    private final ApplicationService appService = new ApplicationService();

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
        String moId   = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");
        int pendingCount = 0;
        try {
            List<ApplicationView> apps = appService.getApplicationsForMO(moId);
            for (ApplicationView a : apps) {
                if ("PENDING".equalsIgnoreCase(a.getStatus())) pendingCount++;
            }
        } catch (Exception ignored) { }
        req.setAttribute("userId",       moId);
        req.setAttribute("userName",     moName);
        req.setAttribute("pendingCount", pendingCount);
        req.getRequestDispatcher("/jsp/mo/home.jsp").forward(req, resp);
    }
}

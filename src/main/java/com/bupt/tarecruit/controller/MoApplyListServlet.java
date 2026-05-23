package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.MoApplyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/mo/apply/list")
public class MoApplyListServlet extends HttpServlet {
    private final MoApplyService moApplyService = new MoApplyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userAccount") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            String role = (String) session.getAttribute("userRole");
            if (!"MO".equalsIgnoreCase(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: MO role required.");
                return;
            }

            String currentMoId = (String) session.getAttribute("userAccount");
            String currentMoName = (String) session.getAttribute("userName");
            String jobId = request.getParameter("jobId");
            if (jobId == null || jobId.trim().isEmpty()) {
                session.setAttribute("moActionError", "Job ID is required.");
                response.sendRedirect(request.getContextPath() + "/mo/positions");
                return;
            }

            List<Application> applyList = moApplyService.getAppListByJobId(jobId, currentMoId);

            request.setAttribute("userId", currentMoId);
            request.setAttribute("userName", currentMoName);
            request.setAttribute("jobId", jobId);
            request.setAttribute("applicantList", applyList);
            request.getRequestDispatcher("/jsp/mo/applicants.jsp").forward(request, response);

        } catch (Exception e) {
            request.getSession().setAttribute("moActionError", "Failed to load applicants: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/positions");
        }
    }
}

package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.MoApplyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/mo/apply/update")
public class MoApplyUpdateServlet extends HttpServlet {
    private final MoApplyService moApplyService = new MoApplyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            String appId = request.getParameter("applicationId");
            String jobId = request.getParameter("jobId");
            String taId = request.getParameter("taId");
            String appliedAt = request.getParameter("appliedAt");
            String statement = request.getParameter("statement");
            String status = request.getParameter("status");
            String feedback = request.getParameter("feedback");

            // 封装对象
            Application app = new Application();
            app.setApplicationId(appId);
            app.setJobId(jobId);
            app.setTaId(taId);
            app.setAppliedAt(appliedAt);
            app.setStatement(statement);
            app.setStatus(status);
            app.setFeedback(feedback);
            app.setMarkedBy(currentMoId);

            moApplyService.updateApplication(app);
            response.sendRedirect(request.getContextPath() + "/mo/job/applicants?jobId=" + jobId);

        } catch (Exception e) {
            request.getSession().setAttribute("moActionError", "Failed to update application: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/applications");
        }
    }
}

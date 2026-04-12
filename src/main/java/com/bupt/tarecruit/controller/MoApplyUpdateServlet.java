package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.MoApplyService;
import com.bupt.tarecruit.util.MoAuthUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/apply/update")
public class MoApplyUpdateServlet extends HttpServlet {
    private final MoApplyService moApplyService = new MoApplyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            // 权限校验
            String currentMoId = MoAuthUtil.getCurrentMoId(request.getSession());
            if (currentMoId == null) {
                response.sendRedirect(request.getContextPath() + "/jsp/motest_login.jsp");
                return;
            }

            // 获取表单参数
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

            // 更新
            moApplyService.updateApplication(app);
            response.sendRedirect(request.getContextPath() + "/mo/apply/list?jobId=" + jobId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
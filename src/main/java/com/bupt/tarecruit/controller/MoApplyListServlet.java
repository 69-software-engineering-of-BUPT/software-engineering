package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.MoApplyService;
import com.bupt.tarecruit.util.MoAuthUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/mo/apply/list")
public class MoApplyListServlet extends HttpServlet {
    private final MoApplyService moApplyService = new MoApplyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            // 权限校验
            String currentMoId = MoAuthUtil.getCurrentMoId(request.getSession());
            if (currentMoId == null) {
                response.sendRedirect(request.getContextPath() + "/jsp/motest_login.jsp");
                return;
            }

            // 获取岗位ID
            String jobId = request.getParameter("jobId");
            List<Application> applyList = moApplyService.getAppListByJobId(jobId, currentMoId);

            request.setAttribute("applyList", applyList);
            request.setAttribute("jobId", jobId);
            request.getRequestDispatcher("/jsp/mo_apply_list.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("msg", "❌ " + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/mo/job/list");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
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

// 左侧导航 Applications 对应路由
@WebServlet("/mo/applications")
public class MOApplicationListServlet extends HttpServlet {
    private final MoApplyService applyService = new MoApplyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 统一登录校验（和项目完全一致）
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            response.sendError(403, "Access denied");
            return;
        }

        // 获取登录用户信息
        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");

        try {
            // ✅ 核心：加载当前MO的【所有申请】，已按时间倒序
            List<Application> allApplications = applyService.getApplicationsForMO(moId);
            int totalCount = allApplications.size();

            // 传递参数
            request.setAttribute("userId", moId);
            request.setAttribute("userName", moName);
            request.setAttribute("applicationList", allApplications);
            request.setAttribute("totalCount", totalCount);

            // 跳转到 applications.jsp
            request.getRequestDispatcher("/jsp/mo/applications.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "加载申请失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/positions");
        }
    }
}
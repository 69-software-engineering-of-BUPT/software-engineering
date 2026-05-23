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
        
        // 统计待处理申请数 + 获取所有申请（用于通知）
        int pendingCount = 0;
        List<ApplicationView> latestApplications = null;
        try {
            latestApplications = appService.getApplicationsForMO(moId);
            for (ApplicationView a : latestApplications) {
                if ("PENDING".equalsIgnoreCase(a.getStatus())) pendingCount++;
            }
        } catch (Exception ignored) { }
        
        // 传递参数
        req.setAttribute("userId",       moId);
        req.setAttribute("userName",     moName);
        req.setAttribute("pendingCount", pendingCount);
        req.setAttribute("latestApps",   latestApplications); // 新增：最新申请列表
        
        req.getRequestDispatcher("/jsp/mo/home.jsp").forward(req, resp);
    }
}
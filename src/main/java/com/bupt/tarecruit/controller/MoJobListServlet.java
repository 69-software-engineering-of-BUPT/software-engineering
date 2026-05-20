package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.MoJobService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/mo/positions")
public class MoJobListServlet extends HttpServlet {
    private final MoJobService moJobService = new MoJobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ===================== 完全照搬 MOHomeServlet 登录校验 =====================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"MO".equalsIgnoreCase(role)) {
            response.sendError(403, "Access denied: MO role required.");
            return;
        }
        // 从Session获取标准登录信息
        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");

        // ===================== 业务逻辑（修复空指针） =====================
        try {
            List<Job> jobList = moJobService.getJobsByMoId(moId);

            // 赋值给JSP
            request.setAttribute("userId", moId);
            request.setAttribute("userName", moName);
            request.setAttribute("jobList", jobList);

            request.getRequestDispatcher("/jsp/mo/positions.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "获取岗位列表失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/home");
        }
    }
}
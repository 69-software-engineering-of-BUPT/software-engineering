package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.MoJobService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;

@WebServlet("/mo/publish")
public class MoJobPublishServlet extends HttpServlet {
    private final MoJobService moJobService = new MoJobService();

    // 打开发布页面
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 完全照搬MOHomeServlet的Session校验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // 从Session取用户信息（和你项目完全一致）
        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");
        
        // 存入request，给页面使用
        request.setAttribute("userId", moId);
        request.setAttribute("userName", moName);
        
        request.getRequestDispatcher("/jsp/mo/publish-position.jsp").forward(request, response);
    }

    // 提交发布
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // ✅ 核心：照搬MOHomeServlet的Session登录校验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ✅ 从Session直接取MO的ID和姓名（永不为空）
        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");

        try {
            // 封装表单数据
            Job job = new Job();
            job.setModuleName(request.getParameter("moduleName"));
            job.setJobType(request.getParameter("jobType"));
            job.setIntroduction(request.getParameter("introduction"));
            job.setRequirements(request.getParameter("requirements"));
            job.setWeeklyWorkload(Integer.parseInt(request.getParameter("weeklyWorkload")));
            job.setDeadline(request.getParameter("deadline"));

            // ===================== 仅新增：招聘人数参数 =====================
            int leaderCount = Integer.parseInt(request.getParameter("leaderCount"));
            int memberCount = Integer.parseInt(request.getParameter("memberCount"));
            job.setLeaderCount(leaderCount);
            job.setMemberCount(memberCount);

            // ✅ 直接传ID和姓名，不再用User对象
            moJobService.publishMoJob(job, moId, moName);

            request.getSession().setAttribute("moActionSuccess", "岗位发布成功！");
            response.sendRedirect(request.getContextPath() + "/mo/positions");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "发布失败：" + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/mo/publish");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
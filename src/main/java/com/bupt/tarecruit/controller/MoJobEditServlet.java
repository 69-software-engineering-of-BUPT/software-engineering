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

@WebServlet("/mo/edit")
public class MoJobEditServlet extends HttpServlet {
    private final MoJobService moJobService = new MoJobService();

    // GET：打开编辑页（无修改）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String moId = (String) session.getAttribute("userAccount");

        try {
            String jobId = request.getParameter("jobId");
            Job job = moJobService.getJobById(jobId, moId);
            request.setAttribute("job", job);
            request.getRequestDispatcher("/jsp/mo/edit-position.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "获取岗位信息失败：" + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/mo/positions");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // POST：保存编辑（✅ 核心修复：保留mdId/mdName）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String moId = (String) session.getAttribute("userAccount");

        try {
            // 1. 先查询【原岗位数据】，保留 mdId / mdName （关键！）
            String jobId = request.getParameter("jobId");
            Job originalJob = moJobService.getJobById(jobId, moId);
            
            // 2. 仅更新表单修改的字段，保留原有负责人信息
            Job job = new Job();
            job.setJobId(jobId);
            job.setMoId(moId);
            // ✅ 保留发布时自动生成的负责人信息（永不修改）
            job.setMdId(originalJob.getMdId());
            job.setMdName(originalJob.getMdName());
            
            // 3. 赋值表单提交的新数据
            job.setModuleName(request.getParameter("moduleName"));
            job.setJobType(request.getParameter("jobType"));
            job.setIntroduction(request.getParameter("introduction"));
            job.setRequirements(request.getParameter("requirements"));
            job.setWeeklyWorkload(Integer.parseInt(request.getParameter("weeklyWorkload")));
            job.setDeadline(request.getParameter("deadline"));
            job.setStatus(request.getParameter("status"));
            job.setPublishedAt(request.getParameter("publishedAt"));

            // ===================== 仅新增：招聘人数参数 =====================
            int leaderCount = Integer.parseInt(request.getParameter("leaderCount"));
            int memberCount = Integer.parseInt(request.getParameter("memberCount"));
            job.setLeaderCount(leaderCount);
            job.setMemberCount(memberCount);

            // 4. 执行更新
            moJobService.updateJob(job);
            request.getSession().setAttribute("moActionSuccess", "岗位修改成功！");
            response.sendRedirect(request.getContextPath() + "/mo/positions");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "修改失败：" + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/mo/positions");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
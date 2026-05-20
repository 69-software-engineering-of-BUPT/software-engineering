package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.service.MoJobService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/mo/job/down")
public class MoJobDownServlet extends HttpServlet {
    private final MoJobService moJobService = new MoJobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 统一登录校验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String moId = (String) session.getAttribute("userAccount");

        try {
            String jobId = request.getParameter("jobId");
            moJobService.downJob(jobId, moId);
            request.getSession().setAttribute("moActionSuccess", "岗位已下架！");
            response.sendRedirect(request.getContextPath() + "/mo/positions");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "下架失败：" + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/mo/positions");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/mo/view/application")
public class MoViewApplicationServlet extends HttpServlet {
    private final MoApplyService applyService = new MoApplyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // 登录校验
        if (session == null || session.getAttribute("userAccount") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 1. 校验申请ID不能为空
        String appId = request.getParameter("applicationId");
        if (appId == null || appId.trim().isEmpty()) {
            request.getSession().setAttribute("moActionError", "申请ID不存在！");
            response.sendRedirect(request.getContextPath() + "/mo/applications");
            return;
        }

        String moId = (String) session.getAttribute("userAccount");
        String moName = (String) session.getAttribute("userName");

        try {
            // 2. 查询申请，查询不到直接跳转
            Application application = applyService.getApplicationById(appId);
            if (application == null) {
                request.getSession().setAttribute("moActionError", "未找到该申请！");
                response.sendRedirect(request.getContextPath() + "/mo/applications");
                return;
            }

            // 3. 正常传递参数
            request.setAttribute("userId", moId);
            request.setAttribute("userName", moName);
            request.setAttribute("application", application);

            request.getRequestDispatcher("/jsp/mo/mo_viewapplication.jsp").forward(request, response);
        } catch (Exception e) {
            // 4. 异常捕获
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "加载申请失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/mo/applications");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String appId = request.getParameter("applicationId");
            String status = request.getParameter("status");
            String applicationType = request.getParameter("applicationType");
            String moMessage = request.getParameter("moMessage");

            Application app = applyService.getApplicationById(appId);
            app.setStatus(status);
            app.setApplicationType(applicationType);

            // 追加消息
            if (moMessage != null && !moMessage.trim().isEmpty()) {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                app.setStatement(app.getStatement() + "\n[" + time + " MO]: " + moMessage.trim());
            }

            applyService.updateApplication(app);
            request.getSession().setAttribute("moActionSuccess", "修改成功！");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("moActionError", "修改失败！");
        }

        response.sendRedirect(request.getContextPath() + "/mo/view/application?applicationId=" + request.getParameter("applicationId"));
    }
}
package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.util.MoAuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/motest/login")
public class MotestLoginServlet extends HttpServlet {
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            String userId = request.getParameter("userId");
            String password = request.getParameter("password");

            // 查询用户
            User user = userRepository.getUserById(userId);
            if (user == null || !user.getPassword().equals(password)) {
                request.setAttribute("msg", "❌ 账号或密码错误");
                request.getRequestDispatcher("/jsp/motest_login.jsp").forward(request, response);
                return;
            }

            // 登录成功：存入Session（对接原有权限系统）
            HttpSession session = request.getSession();
            session.setAttribute(MoAuthUtil.LOGIN_USER_KEY, user);

            // 跳转到 MO 控制台
            response.sendRedirect(request.getContextPath() + "/jsp/mo_index.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
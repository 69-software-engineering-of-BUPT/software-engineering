package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/motest/register")
public class MotestRegisterServlet extends HttpServlet {
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            // 获取表单参数
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            String name = request.getParameter("name");

            // 校验账号是否已存在
            User exist = userRepository.getUserById(userId);
            if (exist != null) {
                request.setAttribute("msg", "❌ 账号已存在！");
                request.getRequestDispatcher("/jsp/motest_register.jsp").forward(request, response);
                return;
            }

            // 封装用户
            User user = new User();
            user.setUserId(userId);
            user.setPassword(password);
            user.setRole(role);
            user.setName(name);

            // 保存到 data/users
            userRepository.saveUser(user);
            request.setAttribute("msg", "✅ 注册成功！请登录");
            request.getRequestDispatcher("/jsp/motest_login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
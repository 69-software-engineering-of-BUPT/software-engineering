package com.bupt.tarecruit.controller;

import com.bupt.tarecruit.util.MoAuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/motest/logout")
public class MotestLogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute(MoAuthUtil.LOGIN_USER_KEY);
        session.invalidate();
        try {
            response.sendRedirect(request.getContextPath() + "/jsp/motest_login.jsp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
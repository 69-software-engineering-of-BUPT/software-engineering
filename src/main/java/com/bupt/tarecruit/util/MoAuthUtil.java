package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.User;
import javax.servlet.http.HttpSession;

/**
 * MO 权限认证工具类（统一处理登录校验、用户获取）
 */
public class MoAuthUtil {
    // 登录用户在Session中的固定KEY（与登录模块约定）
    public static final String LOGIN_USER_KEY = "LOGIN_USER";

    /**
     * 获取当前登录的用户对象
     */
    public static User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(LOGIN_USER_KEY);
    }

    /**
     * 判断：是否为【已登录的MO用户】
     */
    public static boolean isMoLogin(HttpSession session) {
        User user = getLoginUser(session);
        return user != null && "MO".equals(user.getRole());
    }

    /**
     * 获取当前登录MO的userId（无权限返回null）
     */
    public static String getCurrentMoId(HttpSession session) {
        if (!isMoLogin(session)) {
            return null;
        }
        return getLoginUser(session).getUserId();
    }
}
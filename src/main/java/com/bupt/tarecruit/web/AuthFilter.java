package com.bupt.tarecruit.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        String path = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String requiredRole = getRequiredRole(path);
        if (requiredRole == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        String actualRole = normalizeRole(session == null ? null : session.getAttribute("userRole"));
        String userAccount = session == null ? null : trimToNull(session.getAttribute("userAccount"));
        if (actualRole == null || userAccount == null) {
            invalidateSession(session);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        if (!requiredRole.equals(actualRole)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied: " + requiredRole + " role required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
            || "/index.jsp".equals(path)
            || "/jsp/login.jsp".equals(path)
            || "/login".equals(path)
            || "/jsp/register.jsp".equals(path)
            || "/register".equals(path)
            || "/logout".equals(path)
            || path.startsWith("/motest/")
            || path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/images/")
            || path.startsWith("/assets/");
    }

    private String getRequiredRole(String path) {
        if (path.startsWith("/ta/") || path.startsWith("/jsp/ta/")) {
            return "TA";
        }
        if (path.startsWith("/mo/") || path.startsWith("/jsp/mo/")) {
            return "MO";
        }
        if (path.startsWith("/ad/") || path.startsWith("/jsp/ad/")) {
            return "ADMIN";
        }
        return null;
    }

    private String normalizeRole(Object roleValue) {
        if (roleValue == null) {
            return null;
        }
        String role = roleValue.toString().trim().toUpperCase(Locale.ROOT);
        if ("TA".equals(role) || "MO".equals(role) || "ADMIN".equals(role)) {
            return role;
        }
        return null;
    }

    private String trimToNull(Object value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.toString().trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void invalidateSession(HttpSession session) {
        if (session == null) {
            return;
        }
        try {
            session.invalidate();
        } catch (IllegalStateException ignored) {
        }
    }
}

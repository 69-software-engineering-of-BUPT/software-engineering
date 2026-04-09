package com.bupt.tarecruit.web;

import java.io.IOException;

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
        if (session == null || session.getAttribute("userRole") == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        String actualRole = String.valueOf(session.getAttribute("userRole"));
        if (!requiredRole.equals(actualRole)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
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
            || path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/images/");
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
}

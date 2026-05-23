<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruit.service.AuthenticationException" %>
<%@ page import="com.bupt.tarecruit.util.RoleHomeUtil" %>
<%
    // If already signed in, skip the welcome page
    String userId = (String) session.getAttribute("userAccount");
    if (userId != null) {
        try {
            response.sendRedirect(request.getContextPath()
                    + RoleHomeUtil.resolveHomePath((String) session.getAttribute("userRole")));
        } catch (AuthenticationException ex) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return;
    }
    response.sendRedirect(request.getContextPath() + "/login");
%>

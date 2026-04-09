<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // If already signed in, skip the welcome page
    String userId = (String) session.getAttribute("userAccount");
    if (userId != null) {
        String role = (String) session.getAttribute("userRole");
        if ("TA".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/ta/home");
        } else if ("MO".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/mo/home");
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/ad/accounts");
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return;
    }
    response.sendRedirect(request.getContextPath() + "/login");
%>

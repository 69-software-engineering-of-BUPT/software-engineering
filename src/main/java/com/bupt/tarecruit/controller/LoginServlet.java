package com.bupt.tarecruit.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bupt.tarecruit.service.AuthService;
import com.bupt.tarecruit.service.AuthenticatedUser;
import com.bupt.tarecruit.service.AuthenticationException;

/**
 * Handles the sign-in flow for all supported roles.
 * Anonymous users are shown the login page, while authenticated users are
 * redirected to the landing page that matches the role stored in session.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService;

    /**
     * Creates a servlet backed by the default authentication service.
     */
    public LoginServlet() {
        this(new AuthService());
    }

    /**
     * Creates a servlet with an injected authentication service.
     * Package-private visibility keeps constructor injection available for tests.
     *
     * @param authService service used to validate credentials
     */
    LoginServlet(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Shows the login page for anonymous visitors or redirects an existing
     * session to the correct role home page.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userAccount") != null) {
            try {
                resp.sendRedirect(req.getContextPath() + targetFor((String) session.getAttribute("userRole")));
            } catch (AuthenticationException ex) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    /**
     * Authenticates submitted credentials, then stores the canonical session
     * fields {@code userAccount}, {@code userRole}, and {@code userName}.
     * Failed authentication returns the user to the login page with feedback.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String userId = req.getParameter("userId");
        try {
            AuthenticatedUser user = authService.authenticate(userId, req.getParameter("password"));
            HttpSession session = req.getSession(true);
            session.setAttribute("userAccount", user.getUserId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userName", user.getName());
            resp.sendRedirect(req.getContextPath() + targetFor(user.getRole()));
        } catch (AuthenticationException ex) {
            String inputUserId = userId == null ? "" : userId.trim();
            req.setAttribute("loginError", ex.getMessage());
            req.setAttribute("loginUserId", inputUserId);
            req.setAttribute("inputUserId", inputUserId);
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Login failed", ex);
        }
    }

    /**
     * Resolves the landing path for a supported role code.
     *
     * @param role session or account role such as {@code TA}, {@code MO}, or {@code ADMIN}
     * @return application path to the role-specific home page
     * @throws AuthenticationException when the role is not recognised
     */
    private String targetFor(String role) {
        if ("TA".equals(role)) {
            return "/ta/home";
        }
        if ("MO".equals(role)) {
            return "/mo/home";
        }
        if ("ADMIN".equals(role)) {
            return "/ad/accounts";
        }
        throw new AuthenticationException("Unsupported user role.");
    }
}

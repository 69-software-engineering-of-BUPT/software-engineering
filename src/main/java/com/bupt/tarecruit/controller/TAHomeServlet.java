package com.bupt.tarecruit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.TAProfile;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.TAProfileService;

/**
 * TA personal home: profile + application history (prototype: session attribute {@code userAccount}).
 */
@WebServlet("/ta/home")
public class TAHomeServlet extends HttpServlet {
    private final TAProfileService profileService = new TAProfileService();
    private final ApplicationService applicationService = new ApplicationService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }

        try {
            if (req.getSession().getAttribute("profileErrorMsg") != null) {
                req.setAttribute("profileErrorMsg", req.getSession().getAttribute("profileErrorMsg"));
                req.getSession().removeAttribute("profileErrorMsg");
            }
            TAProfile profile = profileService.getProfile(studentId);
            List<ApplicationView> applications = applicationService.getTAApplicationList(studentId);
            req.setAttribute("studentId", studentId);
            req.setAttribute("profile", profile);
            req.setAttribute("applicationList", applications);
            req.setAttribute("applicationListJson", gson.toJson(applications));
            req.getRequestDispatcher("/jsp/ta/home.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Failed to load TA home");
        }
    }
}

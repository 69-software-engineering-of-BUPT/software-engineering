package com.bupt.tarecruit.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves CV PDF files stored under data/uploads/.
 * Maps to /uploads/* so MO can open links like contextPath/uploads/cv_TA001.pdf
 * Storing under data/ keeps files across Maven clean cycles.
 */
@WebServlet("/uploads/*")
public class CvFileServlet extends HttpServlet {

    /** Stable storage directory (relative to working directory = project root). */
    static final String UPLOAD_DIR = "data/uploads/";

    /** Only allow filenames that match cv_<word chars/dash>.pdf to prevent path traversal. */
    private static final Pattern SAFE_NAME = Pattern.compile("^cv_[\\w\\-]+\\.pdf$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Require authenticated session
        if (req.getSession().getAttribute("userAccount") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }

        String pathInfo = req.getPathInfo(); // e.g. "/cv_TA001.pdf"
        if (pathInfo == null || pathInfo.length() < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = pathInfo.substring(1); // strip leading "/"
        if (!SAFE_NAME.matcher(fileName).matches()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid file name.");
            return;
        }

        File file = new File(UPLOAD_DIR, fileName);
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found.");
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file.toPath(), out);
        }
    }
}

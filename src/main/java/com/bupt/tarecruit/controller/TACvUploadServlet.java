package com.bupt.tarecruit.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

/**
 * TA002: Upload CV (PDF only, max 5 MB).
 */
@WebServlet("/ta/uploadCv")
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,       // 5 MB
    maxRequestSize = 6 * 1024 * 1024      // 6 MB total
)
public class TACvUploadServlet extends HttpServlet {
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Part filePart = req.getPart("cvFile");
            if (filePart == null || filePart.getSize() == 0) {
                req.getSession().setAttribute("profileErrorMsg", "Please select a file to upload.");
                resp.sendRedirect(req.getContextPath() + "/ta/home");
                return;
            }

            String fileName = getFileName(filePart);
            if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                req.getSession().setAttribute("profileErrorMsg", "Only PDF files are accepted.");
                resp.sendRedirect(req.getContextPath() + "/ta/home");
                return;
            }

            File uploadDir = new File(getServletContext().getRealPath("/uploads/"));
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String savedName = "cv_" + studentId + ".pdf";

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploadDir.getAbsolutePath(), savedName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Store URL-relative path (accessible as /uploads/cv_xxx.pdf)
            String urlPath = "uploads/" + savedName;

            User user = userRepo.getUserById(studentId);
            if (user != null) {
                user.setCvFilePath(urlPath);
                userRepo.saveUser(user);
            }

            req.getSession().setAttribute("cvSuccess", "CV uploaded successfully.");
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        } catch (IllegalStateException e) {
            req.getSession().setAttribute("profileErrorMsg", "File too large. Maximum size is 5 MB.");
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        }
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                String name = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                int idx = name.lastIndexOf('/');
                if (idx >= 0) name = name.substring(idx + 1);
                idx = name.lastIndexOf('\\');
                if (idx >= 0) name = name.substring(idx + 1);
                return name;
            }
        }
        return null;
    }
}

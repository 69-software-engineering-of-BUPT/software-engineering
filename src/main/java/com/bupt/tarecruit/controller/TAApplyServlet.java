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

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;

/**
 * TA003 + TA005: Submit a new application (with optional statement and CV upload).
 */
@WebServlet("/ta/apply")
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 6 * 1024 * 1024
)
public class TAApplyServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String studentId = (String) req.getSession().getAttribute("userAccount");
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String jobId = getPartValue(req, "jobId");
        String statement = getPartValue(req, "statement");
        String applicationType = getPartValue(req, "applicationType");

        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
            return;
        }

        try {
            Job job = jobService.getJobById(jobId.trim());
            if (job == null || !"OPEN".equalsIgnoreCase(job.getStatus())) {
                req.getSession().setAttribute("applyError", "This position is no longer open for applications.");
                resp.sendRedirect(req.getContextPath() + "/ta/jobs");
                return;
            }

            // AD001: enforce 3-active-job limit
            User user = userRepo.getUserById(studentId);
            if (user != null && user.getActiveJobsCount() >= 3) {
                req.getSession().setAttribute("applyError", "You have reached the maximum of 3 active TA positions.");
                resp.sendRedirect(req.getContextPath() + "/ta/jobs");
                return;
            }

            // Handle CV file upload
            boolean cvUploaded = user != null
                && user.getCvFilePath() != null
                && !user.getCvFilePath().trim().isEmpty();
            Part filePart = req.getPart("cvFile");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = getFileName(filePart);
                if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                    File uploadDir = new File(getServletContext().getRealPath("/uploads/"));
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    String savedName = "cv_" + studentId + ".pdf";
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, Paths.get(uploadDir.getAbsolutePath(), savedName), StandardCopyOption.REPLACE_EXISTING);
                    }
                    String urlPath = "uploads/" + savedName;
                    if (user != null) {
                        user.setCvFilePath(urlPath);
                        userRepo.saveUser(user);
                    }
                    cvUploaded = true;
                } else {
                    req.getSession().setAttribute("applyError", "Only PDF files are accepted for CV.");
                    resp.sendRedirect(req.getContextPath() + "/ta/jobs");
                    return;
                }
            }

            Application app = new Application();
            app.setStudentId(studentId);
            app.setJobId(jobId.trim());
            app.setStatement(statement != null ? statement.trim() : "");
            app.setApplicationType(applicationType != null ? applicationType.trim() : "");
            app.setCvAttached(cvUploaded);

            applicationService.submitApplication(app);

            // Increment active jobs counter
            if (user != null) {
                user.setActiveJobsCount(user.getActiveJobsCount() + 1);
                userRepo.saveUser(user);
            }

            req.getSession().setAttribute("applySuccess", "Application submitted successfully for " + job.getModuleName() + "!");
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
        } catch (IllegalStateException e) {
            req.getSession().setAttribute("applyError", "File too large. Maximum size is 5 MB.");
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("applyError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
        }
    }

    private String getPartValue(HttpServletRequest req, String name) throws IOException, ServletException {
        Part part = req.getPart(name);
        if (part == null) return null;
        try (InputStream is = part.getInputStream()) {
            byte[] bytes = new byte[(int) part.getSize()];
            is.read(bytes);
            return new String(bytes, "UTF-8");
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

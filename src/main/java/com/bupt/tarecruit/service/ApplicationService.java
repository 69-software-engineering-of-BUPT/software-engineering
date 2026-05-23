package com.bupt.tarecruit.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.repository.UserRepository;

public class ApplicationService {
    final private ApplicationRepository appRepo = new ApplicationRepository();
    final private JobRepository jobRepo = new JobRepository();
    final private UserRepository userRepo = new UserRepository();
    private static final int MAX_WORD_LIMIT = 500;
    public void submitApplication(Application app) throws Exception {
        if (app.getStatement() != null && app.getStatement().length() > MAX_WORD_LIMIT) {
            throw new RuntimeException("Statement exceeds the " + MAX_WORD_LIMIT + " character limit.");
        }

        // Check for duplicate application to the same job
        List<Application> existing = appRepo.findByStudentId(app.getStudentId());
        for (Application e : existing) {
            if (app.getJobId().equals(e.getJobId())) {
                throw new RuntimeException("You have already applied for this position.");
            }
        }

        app.setApplicationId(UUID.randomUUID().toString());
        app.setApplyTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        app.setStatus("PENDING"); 
        
        appRepo.save(app);
    }

    public List<ApplicationView> getTAApplicationList(String studentId) throws Exception {
        List<ApplicationView> viewList = new ArrayList<>();
        List<Application> apps = appRepo.findByStudentId(studentId);

        for (Application app : apps) {
            ApplicationView view = new ApplicationView();
            // Fetch associated job details
            Job job = jobRepo.findById(app.getJobId());
            
            view.setApplicationId(app.getApplicationId());
            view.setFeedback(app.getFeedback());
            view.setJobId(app.getJobId());
            view.setStatus(app.getStatus());
            view.setApplyTime(app.getApplyTime());
            view.setApplicationType(app.getApplicationType());
            view.setStatement(app.getStatement());

            if (job != null) {
                view.setModuleName(job.getModuleName());
                view.setMdName(job.getMdName());
            }
            viewList.add(view);
        }
        viewList.sort(Comparator.comparing(ApplicationView::getApplyTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return viewList;
    }

    public void updateStatementFromChat(String appId, String newMessage) throws Exception {
        Application app = appRepo.findById(appId);
        if (app == null) throw new RuntimeException("Application record not found");
        
        if (newMessage.length() > MAX_WORD_LIMIT) {
            throw new RuntimeException("Message exceeds the " + MAX_WORD_LIMIT + " word limit");
        }
        
        // Format the appended message so the frontend can parse it as a conversation flow
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        String formattedMessage = "\n[" + timestamp + " TA]: " + newMessage;
        
        String currentStatement = app.getStatement() == null ? "" : app.getStatement();
        app.setStatement(currentStatement + formattedMessage);
        
        appRepo.save(app);
    }

    /**
     * Load all applications for jobs owned by the given MO (for MO review view).
     */
    public List<ApplicationView> getApplicationsForMO(String moId) throws Exception {
        List<ApplicationView> viewList = new ArrayList<>();
        List<Application> apps = appRepo.findAll();
        for (Application app : apps) {
            Job job = jobRepo.findById(app.getJobId());
            if (!isJobOwnedByMo(job, moId)) continue;
            ApplicationView view = new ApplicationView();
            view.setApplicationId(app.getApplicationId());
            view.setJobId(app.getJobId());
            view.setStatus(app.getStatus());
            view.setApplyTime(app.getApplyTime());
            view.setFeedback(app.getFeedback());
            view.setStatement(app.getStatement());
            view.setApplicationType(app.getApplicationType());
            view.setTaId(app.getStudentId());
            view.setCvAttached(app.isCvAttached());
            // Load TA's actual CV path so MO can open the file
            if (app.isCvAttached()) {
                try {
                    User ta = userRepo.getUserById(app.getStudentId());
                    if (ta != null) view.setCvFilePath(ta.getCvFilePath());
                } catch (Exception ignored) { }
            }
            view.setModuleName(job.getModuleName());
            view.setMdName(job.getMdName());
            viewList.add(view);
        }
        viewList.sort(Comparator.comparing(ApplicationView::getApplyTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return viewList;
    }

    /**
     * Load a single application only if it belongs to a job owned by the MO.
     */
    public Application getApplicationForMO(String appId, String moId) throws Exception {
        Application app = appRepo.findById(appId);
        if (app == null) throw new RuntimeException("Application not found: " + appId);
        requireMoOwnsApplication(app, moId);
        return app;
    }

    /**
     * Load the job behind an application only if that job belongs to the MO.
     */
    public Job getJobForApplicationForMO(String appId, String moId) throws Exception {
        Application app = getApplicationForMO(appId, moId);
        Job job = jobRepo.findById(app.getJobId());
        if (job == null) throw new RuntimeException("Job not found for application: " + appId);
        return job;
    }

    /**
     * MO updates status and optionally adds feedback. Persists change.
     * REJECTED restores the TA's activeJobsCount; moving out of REJECTED occupies the quota again.
     */
    public Application updateApplicationStatus(String appId, String newStatus, String feedback, String moId) throws Exception {
        return updateApplicationStatus(appId, newStatus, feedback, moId, null);
    }

    public Application updateApplicationStatus(String appId, String newStatus, String feedback, String moId, String applicationType) throws Exception {
        Application app = appRepo.findById(appId);
        if (app == null) throw new RuntimeException("Application not found: " + appId);

        requireMoOwnsApplication(app, moId);

        String previousStatus = normalizeStatus(app.getStatus());
        String normalizedStatus = normalizeStatus(newStatus);

        app.setStatus(normalizedStatus);
        if (feedback != null) {
            app.setFeedback(feedback.trim());
        }
        if (!isBlank(applicationType)) {
            app.setApplicationType(applicationType.trim());
        }
        app.setMarkedBy(moId);
        app.setMarkTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        appRepo.save(app);

        updateTaQuotaAfterStatusChange(previousStatus, normalizedStatus, app.getStudentId());
        return app;
    }

    /**
     * MO appends a chat message to the application's statement thread.
     * Format: "\n[yyyy-MM-dd HH:mm MO]: message"
     */
    public void appendMOMessage(String appId, String moId, String message) throws Exception {
        if (message == null || message.trim().isEmpty()) return;
        if (message.trim().length() > MAX_WORD_LIMIT) {
            throw new RuntimeException("Message exceeds the " + MAX_WORD_LIMIT + " character limit.");
        }
        Application app = getApplicationForMO(appId, moId);
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        String formatted = "\n[" + timestamp + " MO]: " + message.trim();
        String current = app.getStatement() == null ? "" : app.getStatement();
        app.setStatement(current + formatted);
        appRepo.save(app);
    }

    private void requireMoOwnsApplication(Application app, String moId) throws Exception {
        Job job = jobRepo.findById(app.getJobId());
        if (!isJobOwnedByMo(job, moId)) {
            throw new RuntimeException("You do not own this application's job position.");
        }
    }

    private boolean isJobOwnedByMo(Job job, String moId) {
        if (job == null || isBlank(moId)) return false;
        return moId.equals(job.getMoId()) || moId.equals(job.getMdId());
    }

    private String normalizeStatus(String status) {
        if (isBlank(status)) {
            throw new RuntimeException("Application status is required.");
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        switch (normalized) {
            case "PENDING":
            case "INTERVIEW":
            case "APPROVED":
            case "REJECTED":
                return normalized;
            default:
                throw new RuntimeException("Unsupported application status: " + status);
        }
    }

    private void updateTaQuotaAfterStatusChange(String previousStatus, String newStatus, String taId) throws Exception {
        boolean previousCounts = countsAgainstTaQuota(previousStatus);
        boolean newCounts = countsAgainstTaQuota(newStatus);
        if (previousCounts == newCounts || isBlank(taId)) return;

        User ta = userRepo.getUserById(taId);
        if (ta == null) return;

        if (previousCounts && !newCounts) {
            ta.setActiveJobsCount(Math.max(0, ta.getActiveJobsCount() - 1));
        } else if (!previousCounts && newCounts) {
            ta.setActiveJobsCount(ta.getActiveJobsCount() + 1);
        }
        userRepo.saveUser(ta);
    }

    private boolean countsAgainstTaQuota(String status) {
        return !"REJECTED".equalsIgnoreCase(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

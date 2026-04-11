package com.bupt.tarecruit.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
            if (job == null || !moId.equals(job.getMdId())) continue;
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
     * MO updates status and optionally adds feedback. Persists change.
     * REJECTED: restores the TA's activeJobsCount (only if previous status was not already REJECTED).
     * APPROVED / INTERVIEW: no change to count (position still occupied or in-progress).
     */
    public void updateApplicationStatus(String appId, String newStatus, String feedback, String moId) throws Exception {
        Application app = appRepo.findById(appId);
        if (app == null) throw new RuntimeException("Application not found: " + appId);

        String previousStatus = app.getStatus();

        app.setStatus(newStatus);
        if (feedback != null && !feedback.trim().isEmpty()) {
            app.setFeedback(feedback.trim());
        }
        app.setMarkedBy(moId);
        app.setMarkTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        appRepo.save(app);

        // Restore TA's quota when rejected (but not if it was already rejected before)
        if ("REJECTED".equalsIgnoreCase(newStatus) && !"REJECTED".equalsIgnoreCase(previousStatus)) {
            String taId = app.getStudentId();
            User ta = userRepo.getUserById(taId);
            if (ta != null && ta.getActiveJobsCount() > 0) {
                ta.setActiveJobsCount(ta.getActiveJobsCount() - 1);
                userRepo.saveUser(ta);
            }
        }
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
        Application app = appRepo.findById(appId);
        if (app == null) throw new RuntimeException("Application not found: " + appId);
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        String formatted = "\n[" + timestamp + " MO]: " + message.trim();
        String current = app.getStatement() == null ? "" : app.getStatement();
        app.setStatement(current + formatted);
        appRepo.save(app);
    }
}
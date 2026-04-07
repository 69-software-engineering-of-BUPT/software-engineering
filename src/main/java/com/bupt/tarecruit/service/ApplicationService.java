package com.bupt.tarecruit.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bupt.tarecruit.model.Application; // Assume Job model exists
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Job; // Assume JobRepository exists
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;

public class ApplicationService {
    final private ApplicationRepository appRepo = new ApplicationRepository();
    final private JobRepository jobRepo = new JobRepository();
    private static final int MAX_WORD_LIMIT = 500;

    /**
     * Get a list of applications with job details for TA004 list view
     */
    public void submitApplication(Application app) throws Exception {
        if (app.getStatement() != null && app.getStatement().length() > MAX_WORD_LIMIT) {
            throw new RuntimeException("Statement exceeds the " + MAX_WORD_LIMIT + " character limit.");
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
}
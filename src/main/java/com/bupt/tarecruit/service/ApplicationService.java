package com.bupt.tarecruit.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.repository.UserRepository;
import com.bupt.tarecruit.util.IdGenerator;
import com.bupt.tarecruit.util.TimeUtil;

public class ApplicationService {
    private static final int MAX_WORD_LIMIT = 500;

    private final ApplicationRepository appRepo;
    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public ApplicationService() {
        this(Paths.get("."));
    }

    public ApplicationService(Path root) {
        this.appRepo = new ApplicationRepository(root);
        this.jobRepo = new JobRepository(root);
        this.userRepo = new UserRepository(root);
        this.notificationService = new NotificationService(root);
    }

    public void submitApplication(Application app) throws Exception {
        if (app == null) {
            throw new ApplicationException("Application is required.");
        }
        if (isBlank(app.getStudentId()) || isBlank(app.getJobId())) {
            throw new ApplicationException("Student ID and job ID are required.");
        }
        if (app.getStatement() != null && app.getStatement().length() > MAX_WORD_LIMIT) {
            throw new ApplicationException("Statement exceeds the 500 character limit.");
        }
        Job job = jobRepo.findById(app.getJobId());
        if (job == null) {
            throw new ApplicationException("Job not found.");
        }
        if (!"OPEN".equalsIgnoreCase(job.getStatus()) || TimeUtil.isExpired(job.getDeadline())) {
            throw new ApplicationException("This job is no longer accepting applications.");
        }
        if (appRepo.existsByStudentIdAndJobId(app.getStudentId(), app.getJobId())) {
            throw new ApplicationException("You have already applied for this job.");
        }

        app.setApplicationId(IdGenerator.newId("APP"));
        app.setApplyTime(TimeUtil.nowDateTime());
        app.setStatus("PENDING");
        if (app.getApplicationType() == null || app.getApplicationType().trim().isEmpty()) {
            app.setApplicationType("Standard");
        }
        appRepo.save(app);
    }

    public List<ApplicationView> getTAApplicationList(String studentId) throws Exception {
        List<ApplicationView> viewList = new ArrayList<ApplicationView>();
        List<Application> apps = appRepo.findByStudentId(studentId);

        for (Application app : apps) {
            viewList.add(toView(app));
        }
        viewList.sort(Comparator.comparing(ApplicationView::getApplyTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return viewList;
    }

    public List<ApplicationView> getApplicationsForJob(String jobId) throws Exception {
        List<ApplicationView> viewList = new ArrayList<ApplicationView>();
        for (Application app : appRepo.findByJobId(jobId)) {
            viewList.add(toView(app));
        }
        viewList.sort(Comparator.comparing(ApplicationView::getApplyTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return viewList;
    }

    public void reviewApplication(String applicationId, String reviewerId, String decision, String feedback) throws Exception {
        Application app = appRepo.findById(applicationId);
        if (app == null) {
            throw new ApplicationException("Application record not found.");
        }
        if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new ApplicationException("Unsupported review decision.");
        }
        app.setStatus(decision);
        app.setFeedback(feedback == null ? "" : feedback.trim());
        app.setMarkedBy(reviewerId);
        app.setMarkTime(TimeUtil.nowDateTime());
        appRepo.save(app);
        notificationService.createStatusUpdate(app.getStudentId(), app.getApplicationId(),
            "Application " + app.getApplicationId() + " was " + decision + (app.getFeedback().isEmpty() ? "." : ": " + app.getFeedback()));
    }

    public void updateStatementFromChat(String appId, String newMessage) throws Exception {
        Application app = appRepo.findById(appId);
        if (app == null) {
            throw new ApplicationException("Application record not found");
        }
        if (newMessage.length() > MAX_WORD_LIMIT) {
            throw new ApplicationException("Message exceeds the 500 word limit");
        }
        String formattedMessage = "\n[" + TimeUtil.nowDateTime().substring(0, 16) + " TA]: " + newMessage;
        String currentStatement = app.getStatement() == null ? "" : app.getStatement();
        app.setStatement(currentStatement + formattedMessage);
        appRepo.save(app);
    }

    private ApplicationView toView(Application app) throws Exception {
        ApplicationView view = new ApplicationView();
        Job job = jobRepo.findById(app.getJobId());
        User student = userRepo.getUserById(app.getStudentId());
        view.setApplicationId(app.getApplicationId());
        view.setFeedback(app.getFeedback());
        view.setJobId(app.getJobId());
        view.setStatus(app.getStatus());
        view.setApplyTime(app.getApplyTime());
        view.setApplicationType(app.getApplicationType());
        view.setStatement(app.getStatement());
        view.setStudentId(app.getStudentId());
        view.setStudentName(student != null ? student.getName() : app.getStudentId());
        if (job != null) {
            view.setModuleName(job.getModuleName());
            view.setMdName(job.getMdName() != null && !job.getMdName().trim().isEmpty() ? job.getMdName() : resolveMoName(job.getMoId()));
        }
        return view;
    }

    private String resolveMoName(String moId) throws Exception {
        if (isBlank(moId)) {
            return null;
        }
        User mo = userRepo.getUserById(moId);
        return mo != null ? mo.getName() : moId;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

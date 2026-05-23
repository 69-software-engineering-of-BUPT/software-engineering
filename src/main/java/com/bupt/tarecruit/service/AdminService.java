package com.bupt.tarecruit.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.OperationLog;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.repository.JobRepository;
import com.bupt.tarecruit.repository.OperationLogRepository;
import com.bupt.tarecruit.repository.UserRepository;

public class AdminService {
    private static final int TA_ACTIVE_JOB_LIMIT = 3;
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final OperationLogRepository operationLogRepository = new OperationLogRepository();
    private final UserRepository userRepository = new UserRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    public List<Map<String, Object>> getAccountViews() throws IOException {
        List<Map<String, Object>> accountViews = new ArrayList<>();
        for (User u : userRepository.getAllUsers()) {
            Map<String, Object> view = new LinkedHashMap<>();
            view.put("userId", u.getUserId());
            view.put("role", u.getRole());
            view.put("name", u.getName());
            view.put("cvFilePath", u.getCvFilePath());
            view.put("activeJobsCount", u.getActiveJobsCount());
            view.put("status", defaultIfBlank(u.getStatus(), "ACTIVE"));

            String studentId = u.getUserId();
            String fullName = defaultIfBlank(u.getName(), u.getUserId());
            String email = defaultIfBlank(u.getEmail(), u.getUserId());
            String phoneNumber = defaultIfBlank(u.getPhoneNumber(), "-");
            String researchArea = defaultIfBlank(u.getResearchArea(), "-");
            String cet6Grade = defaultIfBlank(u.getCet6Grade(), "-");

            view.put("studentId", studentId);
            view.put("fullName", fullName);
            view.put("email", email);
            view.put("phoneNumber", phoneNumber);
            view.put("researchArea", researchArea);
            view.put("cet6Grade", cet6Grade);
            accountViews.add(view);
        }
        return accountViews;
    }

    public void freezeAccount(String targetUserId, String actorId, String actorName, String actorRole) throws IOException {
        User user = requireEditableUser(targetUserId);
        user.setStatus("FROZEN");
        userRepository.saveUser(user);
        recordOperationLog(actorId, actorName, actorRole, "ACCOUNT_FROZEN", "USER", user.getUserId(), displayName(user), "WARNING",
                "Administrator froze account " + user.getUserId());
    }

    public void unfreezeAccount(String targetUserId, String actorId, String actorName, String actorRole) throws IOException {
        User user = requireEditableUser(targetUserId);
        user.setStatus("ACTIVE");
        userRepository.saveUser(user);
        recordOperationLog(actorId, actorName, actorRole, "ACCOUNT_UNFROZEN", "USER", user.getUserId(), displayName(user), "SUCCESS",
                "Administrator unfroze account " + user.getUserId());
    }

    public void deleteAccount(String targetUserId, String actorId, String actorName, String actorRole) throws IOException {
        User user = requireEditableUser(targetUserId);
        userRepository.deleteUser(user.getUserId());
        recordOperationLog(actorId, actorName, actorRole, "ACCOUNT_DELETED", "USER", user.getUserId(), displayName(user), "WARNING",
                "Administrator deleted account " + user.getUserId());
    }

    public List<OperationLog> getOperationLogs() throws IOException {
        return operationLogRepository.getAllLogs();
    }

    public List<Map<String, Object>> getProjectViews() throws Exception {
        List<Job> jobs = jobRepository.getAllJobs();
        List<Application> applications = applicationRepository.findAll();
        List<Map<String, Object>> projectViews = new ArrayList<>();

        for (Job job : jobs) {
            if (job == null) {
                continue;
            }

            List<Application> jobApplications = applications.stream()
                    .filter(application -> application != null && sameId(job.getJobId(), application.getJobId()))
                    .collect(Collectors.toList());

            long approvedCount = countApprovedApplications(jobApplications);
            long leaderFilled = countApprovedApplicationsByType(jobApplications, "L");
            long memberFilled = countApprovedApplicationsByType(jobApplications, "NL");
            int leaderSeats = Math.max(job.getLeaderCount(), 0);
            int memberSeats = Math.max(job.getMemberCount(), 0);
            int seats = leaderSeats + memberSeats;
            int vacancies = seats == 0 ? 0 : Math.max(seats - (int) approvedCount, 0);
            boolean actionNeeded = "OPEN".equalsIgnoreCase(job.getStatus()) && seats > 0 && vacancies > 0;

            Map<String, Object> view = new LinkedHashMap<>();
            view.put("jobId", defaultIfBlank(job.getJobId(), "-"));
            view.put("module", defaultIfBlank(job.getModuleName(), "-"));
            view.put("moduleCode", defaultIfBlank(job.getJobType(), defaultIfBlank(job.getJobId(), "-")));
            view.put("moId", defaultIfBlank(defaultIfBlank(job.getMdId(), job.getMoId()), "-"));
            view.put("mo", defaultIfBlank(job.getMdName(), defaultIfBlank(defaultIfBlank(job.getMdId(), job.getMoId()), "-")));
            view.put("posted", defaultIfBlank(job.getPublishedAt(), "-"));
            view.put("deadline", defaultIfBlank(job.getDeadline(), "-"));
            view.put("seats", seats);
            view.put("leaderSeats", leaderSeats);
            view.put("memberSeats", memberSeats);
            view.put("filled", approvedCount);
            view.put("leaderFilled", leaderFilled);
            view.put("memberFilled", memberFilled);
            view.put("vacancies", vacancies);
            view.put("applicationCount", jobApplications.size());
            view.put("statusText", actionNeeded ? "Action Needed" : defaultIfBlank(job.getStatus(), "Filled"));
            view.put("statusClass", actionNeeded ? "warning" : "success");
            view.put("liveDays", "-");
            view.put("requirements", defaultIfBlank(job.getRequirements(), "-"));
            view.put("details", defaultIfBlank(job.getIntroduction(), "-"));
            view.put("approvedTas", buildApplicationSummary(jobApplications, "APPROVED"));
            view.put("pendingTas", buildPendingApplicationSummary(jobApplications));
            projectViews.add(view);
        }

        return projectViews;
    }

    public OperationLog recordOperationLog(
            String actorId,
            String actorName,
            String actorRole,
            String actionType,
            String targetType,
            String targetId,
            String targetName,
            String result,
            String message) throws IOException {
        String normalizedAction = normalizeActionType(actionType);
        String normalizedResult = isBlank(result) ? defaultResult(normalizedAction) : result.trim().toUpperCase();

        OperationLog log = new OperationLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setCreatedAt(LocalDateTime.now().format(LOG_TIME_FORMAT));
        log.setActorId(defaultIfBlank(actorId, "ADMIN"));
        log.setActorName(defaultIfBlank(actorName, "System Admin"));
        log.setActorRole(defaultIfBlank(actorRole, "AD"));
        log.setActionType(normalizedAction);
        log.setActionLabel(actionLabel(normalizedAction));
        log.setTargetType(defaultIfBlank(targetType, "GENERAL"));
        log.setTargetId(defaultIfBlank(targetId, "-"));
        log.setTargetName(defaultIfBlank(targetName, "-"));
        log.setResult(normalizedResult);
        log.setResultClass(resultClass(normalizedResult, normalizedAction));
        log.setMessage(defaultIfBlank(message, buildMessage(normalizedAction, log.getTargetName())));

        operationLogRepository.save(log);
        return log;
    }

    public boolean isTaAtUpperLimit(User user) {
        return user != null
                && "TA".equalsIgnoreCase(user.getRole())
                && user.getActiveJobsCount() >= TA_ACTIVE_JOB_LIMIT;
    }

    public List<User> filterUsersByRole(List<User> users, String role) {
        if (users == null || role == null || "all".equalsIgnoreCase(role)) {
            return users == null ? Collections.emptyList() : users;
        }

        String expectedRole = role.toUpperCase(Locale.ROOT);
        return users.stream()
                .filter(user -> user != null && expectedRole.equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<Job> filterOpenJobs(List<Job> jobs) {
        if (jobs == null) {
            return Collections.emptyList();
        }

        return jobs.stream()
                .filter(job -> job != null && "OPEN".equalsIgnoreCase(job.getStatus()))
                .collect(Collectors.toList());
    }

    public long countApprovedApplications(List<Application> applications) {
        if (applications == null) {
            return 0;
        }

        return applications.stream()
                .filter(application -> application != null
                        && "APPROVED".equalsIgnoreCase(application.getStatus()))
                .count();
    }

    private long countApprovedApplicationsByType(List<Application> applications, String type) {
        if (applications == null) {
            return 0;
        }

        return applications.stream()
                .filter(application -> application != null
                        && "APPROVED".equalsIgnoreCase(application.getStatus())
                        && type.equalsIgnoreCase(defaultIfBlank(application.getApplicationType(), "")))
                .count();
    }

    private String buildApplicationSummary(List<Application> applications, String status) {
        if (applications == null) {
            return "";
        }

        return applications.stream()
                .filter(application -> application != null && status.equalsIgnoreCase(application.getStatus()))
                .map(application -> defaultIfBlank(application.getStudentId(), "-") + "|-|" + defaultIfBlank(application.getApplicationType(), "-"))
                .collect(Collectors.joining(";"));
    }

    private String buildPendingApplicationSummary(List<Application> applications) {
        if (applications == null) {
            return "";
        }

        return applications.stream()
                .filter(application -> application != null && !"APPROVED".equalsIgnoreCase(application.getStatus()))
                .map(application -> defaultIfBlank(application.getStudentId(), "-") + "|-|" + defaultIfBlank(application.getStatus(), "PENDING"))
                .collect(Collectors.joining(";"));
    }

    public boolean isJobActionNeeded(Job job, List<Application> applications) {
        return job != null
                && "OPEN".equalsIgnoreCase(job.getStatus())
                && countApprovedApplications(applications) == 0;
    }

    private String normalizeActionType(String actionType) {
        if (isBlank(actionType)) {
            return "REMINDER_SENT";
        }
        return actionType.trim().replace('-', '_').replace(' ', '_').toUpperCase();
    }

    private String actionLabel(String actionType) {
        switch (actionType) {
            case "ACCOUNT_DELETED":
                return "Account Deleted";
            case "ACCOUNT_FROZEN":
                return "Account Frozen";
            case "ACCOUNT_UNFROZEN":
                return "Account Unfrozen";
            case "REMINDER_SENT":
                return "Reminder Sent";
            case "APPLICATION_SUBMITTED":
                return "Application Submitted";
            case "SUPPLEMENT_REQUESTED":
                return "Supplement Requested";
            case "APPLICATION_APPROVED":
                return "Application Approved";
            case "CSV_EXPORT":
                return "CSV Export";
            default:
                String lower = actionType.toLowerCase().replace('_', ' ');
                return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }
    }

    private String defaultResult(String actionType) {
        if ("ACCOUNT_DELETED".equals(actionType) || "ACCOUNT_FROZEN".equals(actionType)) {
            return "WARNING";
        }
        return "SUCCESS";
    }

    private String resultClass(String result, String actionType) {
        if ("WARNING".equalsIgnoreCase(result) || "FAILED".equalsIgnoreCase(result)) {
            return "warning";
        }
        if ("ACCOUNT_DELETED".equals(actionType) || "ACCOUNT_FROZEN".equals(actionType)) {
            return "warning";
        }
        return "success";
    }

    private String buildMessage(String actionType, String targetName) {
        return actionLabel(actionType) + ": " + defaultIfBlank(targetName, "-");
    }

    private User requireEditableUser(String userId) throws IOException {
        if (isBlank(userId)) {
            throw new IllegalArgumentException("Missing userId.");
        }
        String normalizedUserId = userId.trim();
        if (!normalizedUserId.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid userId.");
        }
        User user = userRepository.getUserById(normalizedUserId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Administrator accounts cannot be changed here.");
        }
        return user;
    }

    private String displayName(User user) {
        if (user == null) {
            return "-";
        }
        return defaultIfBlank(user.getName(), user.getUserId());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value.trim();
    }

    private boolean sameId(String left, String right) {
        return !isBlank(left) && !isBlank(right) && left.trim().equals(right.trim());
    }
}

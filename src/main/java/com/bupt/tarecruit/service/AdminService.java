package com.bupt.tarecruit.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bupt.tarecruit.model.OperationLog;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.OperationLogRepository;
import com.bupt.tarecruit.repository.UserRepository;

public class AdminService {
    private final OperationLogRepository operationLogRepository = new OperationLogRepository();
    private final UserRepository userRepository = new UserRepository();
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
}

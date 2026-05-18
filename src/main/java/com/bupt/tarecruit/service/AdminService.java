package com.bupt.tarecruit.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.bupt.tarecruit.model.OperationLog;
import com.bupt.tarecruit.repository.OperationLogRepository;

public class AdminService {
    private final OperationLogRepository operationLogRepository = new OperationLogRepository();
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value.trim();
    }
}

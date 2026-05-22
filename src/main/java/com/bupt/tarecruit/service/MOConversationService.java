package com.bupt.tarecruit.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.ConversationMessage;
import com.bupt.tarecruit.model.ConversationThread;

public class MOConversationService {
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^\\[(.+)\\s+(TA|MO)\\]:\\s*(.*)$");
    private final ApplicationService applicationService = new ApplicationService();
    private final MOConversationReadService readService = new MOConversationReadService();

    public List<ConversationThread> getThreadsForMO(String moId) throws Exception {
        List<ApplicationView> applications = applicationService.getApplicationsForMO(moId);
        List<ConversationThread> threads = new ArrayList<>();

        for (ApplicationView app : applications) {
            ConversationThread thread = new ConversationThread();
            thread.setApplicationId(app.getApplicationId());
            thread.setJobId(app.getJobId());
            thread.setModuleName(app.getModuleName());
            thread.setTaId(app.getTaId());
            thread.setStatus(app.getStatus());
            thread.setApplicationType(app.getApplicationType());
            thread.setApplyTime(app.getApplyTime());
            thread.setFeedback(app.getFeedback());
            thread.getMessages().addAll(parseMessages(app.getStatement(), app.getApplyTime()));
            thread.setNeedsMoReply(latestMessageIsUnreadTA(moId, thread));
            threads.add(thread);
        }

        threads.sort(Comparator
                .comparing(ConversationThread::isNeedsMoReply).reversed()
                .thenComparing(ConversationThread::getLatestMessageTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return threads;
    }

    public int countUnreadThreads(String moId) throws Exception {
        int count = 0;
        for (ConversationThread thread : getThreadsForMO(moId)) {
            if (thread.isNeedsMoReply()) count++;
        }
        return count;
    }

    public void markThreadRead(String moId, String applicationId) throws Exception {
        Application app = applicationService.getApplicationForMO(applicationId, moId);
        List<ConversationMessage> messages = parseMessages(app.getStatement(), app.getApplyTime());
        readService.markLatestTaMessageRead(moId, app, messages);
    }

    private List<ConversationMessage> parseMessages(String statement, String applyTime) {
        List<ConversationMessage> messages = new ArrayList<>();
        if (isBlank(statement)) return messages;

        String normalized = statement.replace("\r\n", "\n").replace("\r", "\n");
        String[] lines = normalized.split("\n");
        StringBuilder initialStatement = new StringBuilder();

        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.isEmpty()) continue;

            Matcher matcher = MESSAGE_PATTERN.matcher(line);
            if (matcher.matches()) {
                addInitialStatement(messages, initialStatement, applyTime);
                messages.add(new ConversationMessage(matcher.group(2), matcher.group(1), matcher.group(3)));
            } else if (messages.isEmpty()) {
                if (initialStatement.length() > 0) initialStatement.append('\n');
                initialStatement.append(line);
            } else {
                ConversationMessage latest = messages.get(messages.size() - 1);
                latest.setContent(latest.getContent() + "\n" + line);
            }
        }

        addInitialStatement(messages, initialStatement, applyTime);
        return messages;
    }

    private void addInitialStatement(List<ConversationMessage> messages, StringBuilder initialStatement, String applyTime) {
        if (initialStatement.length() == 0) return;
        messages.add(new ConversationMessage("TA", applyTime, initialStatement.toString()));
        initialStatement.setLength(0);
    }

    private boolean latestMessageIsUnreadTA(String moId, ConversationThread thread) throws Exception {
        ConversationMessage latest = thread.getLatestMessage();
        return latest != null && latest.isFromTA()
                && !readService.isRead(moId, thread.getApplicationId(), latest);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package com.bupt.tarecruit.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bupt.tarecruit.model.ApplicationView;
import com.bupt.tarecruit.model.ConversationReadState;
import com.bupt.tarecruit.util.JsonUtil;

/**
 * Tracks which MO replies a TA has already seen, per application.
 * Persisted to data/conversation-read/READ_TA_{taId}.json
 *
 * Mirrors MOConversationReadService but from the TA side: the "newest MO
 * message" replaces the "newest TA message" as the unit being marked read.
 */
public class TAConversationReadService {
    private static final String DATA_DIR = "data/conversation-read/";
    private static final Pattern REPLY_PATTERN =
            Pattern.compile("^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}) (TA|MO)\\]:\\s*(.*)$");

    private final ApplicationService applicationService = new ApplicationService();

    /**
     * Returns true when the latest message in this conversation is from MO
     * and the TA has NOT yet acknowledged that exact MO message.
     */
    public boolean needsTaAttention(String taId, String applicationId, String statement) {
        if (isBlank(taId) || isBlank(applicationId) || statement == null) return false;
        if (!"MO".equals(latestSender(statement))) return false;
        String latestKey = latestMoMessageKey(statement);
        if (latestKey == null) return false;
        String readKey = getState(taId).getReadMessageKeys().get(applicationId);
        return !latestKey.equals(readKey);
    }

    /**
     * Marks the latest MO message of the given application as seen by this TA.
     * No-op when there is no MO message yet.
     */
    public void markLatestMoMessageRead(String taId, String applicationId, String statement) throws Exception {
        if (isBlank(taId) || isBlank(applicationId)) return;
        String latestKey = latestMoMessageKey(statement);
        if (latestKey == null) return;
        ConversationReadState state = getState(taId);
        state.getReadMessageKeys().put(applicationId, latestKey);
        JsonUtil.saveToJsonFile(state, statePath(taId));
    }

    /**
     * Snapshot of (applicationId -> latest acknowledged MO message key).
     * JSPs use this to decide whether to render the "new" indicator per row.
     */
    public Map<String, String> snapshotReadKeys(String taId) {
        return new HashMap<String, String>(getState(taId).getReadMessageKeys());
    }

    /** Extract the key (sentAt|content) of the most recent MO message, or null. */
    public String latestMoMessageKey(String statement) {
        if (statement == null) return null;
        String[] lines = statement.split("\\r?\\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String t = lines[i].trim();
            if (t.isEmpty()) continue;
            Matcher m = REPLY_PATTERN.matcher(t);
            if (m.matches() && "MO".equals(m.group(2))) {
                return m.group(1) + "|" + m.group(3);
            }
        }
        return null;
    }

    /** Counts conversations where the TA has unseen MO replies. */
    public int countUnreadThreads(String taId) throws Exception {
        if (isBlank(taId)) return 0;
        List<ApplicationView> apps = applicationService.getTAApplicationList(taId);
        if (apps == null) return 0;
        int count = 0;
        for (ApplicationView a : apps) {
            if (needsTaAttention(taId, a.getApplicationId(), a.getStatement())) count++;
        }
        return count;
    }

    /** Last sender of the conversation; defaults to TA when there are no bracketed replies. */
    private String latestSender(String statement) {
        if (statement == null) return "TA";
        String[] lines = statement.split("\\r?\\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String t = lines[i].trim();
            if (t.isEmpty()) continue;
            Matcher m = REPLY_PATTERN.matcher(t);
            if (m.matches()) return m.group(2);
            return "TA";
        }
        return "TA";
    }

    private ConversationReadState getState(String taId) {
        try {
            ConversationReadState state = JsonUtil.readFromJsonFile(statePath(taId), ConversationReadState.class);
            if (state == null) {
                state = new ConversationReadState();
                state.setMoId(taId);
            }
            if (state.getMoId() == null) state.setMoId(taId);
            return state;
        } catch (Exception e) {
            ConversationReadState s = new ConversationReadState();
            s.setMoId(taId);
            return s;
        }
    }

    private String statePath(String taId) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
        String safe = taId == null ? "UNKNOWN" : taId.replaceAll("[^A-Za-z0-9_-]", "_");
        return DATA_DIR + "READ_TA_" + safe + ".json";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

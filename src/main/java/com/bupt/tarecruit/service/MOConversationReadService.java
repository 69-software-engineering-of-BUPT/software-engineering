package com.bupt.tarecruit.service;

import java.io.File;
import java.util.List;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ConversationMessage;
import com.bupt.tarecruit.model.ConversationReadState;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.util.JsonUtil;

public class MOConversationReadService {
    private static final String DATA_DIR = "data/conversation-read/";
    private final ApplicationRepository appRepo = new ApplicationRepository();

    public boolean isRead(String moId, String applicationId, ConversationMessage latestTaMessage) throws Exception {
        if (isBlank(moId) || isBlank(applicationId) || latestTaMessage == null) return false;
        String readKey = getState(moId).getReadMessageKeys().get(applicationId);
        return messageKey(latestTaMessage).equals(readKey);
    }

    public void markLatestTaMessageRead(String moId, Application app, List<ConversationMessage> messages) throws Exception {
        if (isBlank(moId) || app == null || messages == null) return;
        ConversationMessage latestTaMessage = latestTaMessage(messages);
        if (latestTaMessage == null) return;

        ConversationReadState state = getState(moId);
        state.getReadMessageKeys().put(app.getApplicationId(), messageKey(latestTaMessage));
        JsonUtil.saveToJsonFile(state, statePath(moId));
    }

    public void markApplicationRead(String moId, String applicationId, List<ConversationMessage> messages) throws Exception {
        if (isBlank(moId) || isBlank(applicationId) || messages == null) return;
        Application app = appRepo.findById(applicationId);
        if (app == null) return;
        markLatestTaMessageRead(moId, app, messages);
    }

    public ConversationMessage latestTaMessage(List<ConversationMessage> messages) {
        if (messages == null) return null;
        for (int i = messages.size() - 1; i >= 0; i--) {
            ConversationMessage message = messages.get(i);
            if (message != null && message.isFromTA()) {
                return message;
            }
        }
        return null;
    }

    public String messageKey(ConversationMessage message) {
        if (message == null) return "";
        return safe(message.getSentAt()) + "|" + safe(message.getContent());
    }

    private ConversationReadState getState(String moId) throws Exception {
        String path = statePath(moId);
        ConversationReadState state = JsonUtil.readFromJsonFile(path, ConversationReadState.class);
        if (state == null) {
            state = new ConversationReadState();
            state.setMoId(moId);
            return state;
        }
        if (state.getMoId() == null) {
            state.setMoId(moId);
        }
        return state;
    }

    private String statePath(String moId) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
        String safeMoId = moId == null ? "UNKNOWN" : moId.replaceAll("[^A-Za-z0-9_-]", "_");
        return DATA_DIR + "READ_" + safeMoId + ".json";
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

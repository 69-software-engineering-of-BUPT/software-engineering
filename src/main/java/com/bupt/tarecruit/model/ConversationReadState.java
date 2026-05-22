package com.bupt.tarecruit.model;

import java.util.HashMap;
import java.util.Map;

public class ConversationReadState {
    private String moId;
    private Map<String, String> readMessageKeys = new HashMap<>();

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public Map<String, String> getReadMessageKeys() {
        if (readMessageKeys == null) {
            readMessageKeys = new HashMap<>();
        }
        return readMessageKeys;
    }

    public void setReadMessageKeys(Map<String, String> readMessageKeys) {
        this.readMessageKeys = readMessageKeys;
    }
}

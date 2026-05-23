package com.bupt.tarecruit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.After;
import org.junit.Test;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ConversationMessage;
import com.bupt.tarecruit.model.ConversationReadState;
import com.bupt.tarecruit.repository.ApplicationRepository;
import com.bupt.tarecruit.util.JsonUtil;

public class MOConversationReadServiceTest {
    private final MOConversationReadService conversationReadService = new MOConversationReadService();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    private final String moId = "MO_CONV_TEST";
    private final String applicationId = "APP_CONV_TEST";
    private final String stateFile = "data/conversation-read/READ_" + moId + ".json";

    @After
    public void tearDown() {
        new File(stateFile).delete();
        new File("data/applications/" + applicationId + "_application.json").delete();
    }

    @Test
    public void markLatestTaMessageReadPersistsReadKeyForApplication() throws Exception {
        Application app = new Application();
        app.setApplicationId(applicationId);
        applicationRepository.save(app);

        ConversationMessage taMessage = new ConversationMessage("TA", "2026-05-24 10:00", "Hello MO");
        conversationReadService.markLatestTaMessageRead(moId, app, Arrays.asList(taMessage));

        ConversationReadState persisted = JsonUtil.readFromJsonFile(stateFile, ConversationReadState.class);

        assertEquals(moId, persisted.getMoId());
        assertEquals(1, persisted.getReadMessageKeys().size());
        assertEquals("2026-05-24 10:00|Hello MO", persisted.getReadMessageKeys().get(applicationId));
    }

    @Test
    public void isReadReturnsTrueWhenLatestTaMessageMatchesSavedState() throws Exception {
        Application app = new Application();
        app.setApplicationId(applicationId);
        applicationRepository.save(app);

        ConversationMessage taMessage = new ConversationMessage("TA", "2026-05-24 11:00", "Are you available?");
        conversationReadService.markLatestTaMessageRead(moId, app, Arrays.asList(taMessage));

        boolean read = conversationReadService.isRead(moId, applicationId, taMessage);
        assertTrue(read);
    }
}

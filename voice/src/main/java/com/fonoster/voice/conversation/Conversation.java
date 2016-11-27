package com.fonoster.voice.conversation;

import com.ibm.watson.developer_cloud.conversation.v1_experimental.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1_experimental.model.MessageResponse;

import java.util.Collections;
import java.util.List;

public class Conversation {
    private ConversationService service;
    private MessageRequest newMessage;
    private String workspaceId;

    public Conversation() {
        service = new ConversationService(ConversationService.VERSION_DATE_2016_05_19);
        service.setEndPoint("https://gateway.watsonplatform.net/conversation/api");
    }

    public Conversation login(String username, String password) {
        service.setUsernameAndPassword(username, password);
        return this;
    }

    public Conversation workspace(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    public Conversation input(String input) {
        newMessage = new MessageRequest.Builder().inputText(input).build();
        return this;
    }

    public void then(JSFunc func) {
        MessageResponse response = service.message(workspaceId, newMessage).execute();

        List<MessageResponse.Intent> intents = response.getIntents();
        List<MessageResponse.Entity> entities = response.getEntities();

        // Warning: It may be better to just return a result with low confidence
        // If more than one intent is detected.
        Collections.sort(intents,
            (intent2, intent1) -> intent1.getConfidence()
                .compareTo(intent2.getConfidence()));

        MessageResponse.Intent i = intents.get(0);
        Result r = new Result(i.getIntent(), i.getConfidence(), entities);
        func.r(r);
    }

    public interface JSFunc {
        void r(Result r);
    }

    public static class Result {
        private final String intent;
        private final double confidence;
        private final List<MessageResponse.Entity> entities;

        public Result(final String intent, final double confidence) {
            this.intent = intent;
            this.confidence = confidence;
            entities = null;
        }

        public Result(final String intent,
            final double confidence,
            final List<MessageResponse.Entity> entities) {

            this.intent = intent;
            this.confidence = confidence;
            this.entities = entities;
        }

        public String getIntent() {
            return intent;
        }

        public double getConfidence() {
            return confidence;
        }

        public List<MessageResponse.Entity> getEntities() {
            return entities;
        }
    }
}

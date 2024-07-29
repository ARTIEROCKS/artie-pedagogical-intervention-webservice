package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.stubs.ChatGrpc;
import artie.pedagogicalintervention.webservice.stubs.ChatRequest;
import artie.pedagogicalintervention.webservice.stubs.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatClientService {

    private final Logger logger = LoggerFactory.getLogger(ChatClientService.class);
    private final ChatGrpc.ChatBlockingStub chatClient;

    public ChatClientService(ChatGrpc.ChatBlockingStub chatClient) {
        this.chatClient = chatClient;
    }

    public String getResponse(String userId, String contextId, String message, String prompt) {
        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .setPrompt(prompt)
                .build();
        ChatResponse response = chatClient.getResponse(request);
        return response.getReply();
    }
}
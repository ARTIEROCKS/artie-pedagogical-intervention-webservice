package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.stubs.ChatGrpc;
import artie.pedagogicalintervention.webservice.stubs.ChatRequest;
import artie.pedagogicalintervention.webservice.stubs.ChatResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatClientService {

    private final Logger logger = LoggerFactory.getLogger(ChatClientService.class);

    @GrpcClient("chat")
    private ChatGrpc.ChatBlockingStub chatClient;

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
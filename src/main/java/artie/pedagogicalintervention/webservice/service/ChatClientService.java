package artie.pedagogicalintervention.webservice.service;

import chat.ChatGrpc.ChatBlockingStub;
import chat.ChatOuterClass.ChatRequest;
import chat.ChatOuterClass.ChatResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ChatClientService {

    @GrpcClient("chat")
    private ChatBlockingStub chatBlockingStub;
    private Logger logger;

    public String getResponse(String userId, String contextId, String message, String prompt) {
        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .setPrompt(prompt)
                .build();
        ChatResponse response = this.chatBlockingStub.getResponse(request);
        return response.getReply();
    }
}
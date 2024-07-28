package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import chat.ChatGrpc.ChatBlockingStub;
import chat.ChatOuterClass.ChatRequest;
import chat.ChatOuterClass.ChatResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatClientService {

    @GrpcClient("chatService")
    private ChatBlockingStub chatBlockingStub;
    private Logger logger;

    public String getResponse(String userId, String contextId, String message, String prompt, PedagogicalSoftwareData psd) {
        if (chatBlockingStub == null) {
            logger.error("chatBlockingStub is null");
            throw new IllegalStateException("chatBlockingStub is not initialized");
        }

        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .setPrompt(prompt)
                .build();
        ChatResponse response = chatBlockingStub.getResponse(request);
        return response.getReply();
    }
}

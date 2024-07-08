package artie.pedagogicalintervention.webservice.service;

import chat.ChatGrpc.ChatBlockingStub;
import chat.ChatOuterClass.ChatRequest;
import chat.ChatOuterClass.ChatResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class ChatClientService {

    @GrpcClient("chatService")
    private ChatBlockingStub chatBlockingStub;

    public String getResponse(String userId, String contextId, String message) {
        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .build();

        ChatResponse response = chatBlockingStub.getResponse(request);
        return response.getReply();
    }
}

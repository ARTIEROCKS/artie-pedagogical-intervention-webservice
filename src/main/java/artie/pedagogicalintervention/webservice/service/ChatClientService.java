package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.dto.MessageDTO;
import chat.ChatGrpc.ChatBlockingStub;
import chat.ChatOuterClass.ChatRequest;
import chat.ChatOuterClass.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatClientService {

    @GrpcClient("chatService")
    private ChatBlockingStub chatBlockingStub;

    @Value("${artie.webservices.conversations.queue}")
    private String queue;
    private final ObjectMapper objectMapper;
    private Logger logger;

    @Autowired
    public ChatClientService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getResponse(String userId, String contextId, String message) {
        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .build();

        ChatResponse response = chatBlockingStub.getResponse(request);
        return response.getReply();
    }

    @RabbitListener(queues = "${artie.webservices.conversations.queue}")
    public void receiveMessage(String messageContent) {
        try {
            // Parse the message content
            MessageDTO message = objectMapper.readValue(messageContent, MessageDTO.class);

            // Gets the answer from the chat
            String reply = getResponse(message.getUserId(), message.getContextId(), message.getMessage());

            // Log or handle the reply as needed
            logger.info("Reply: " + reply);

            //TODO: Add the logic to perform a pedagogical intervention

        } catch (Exception e) {
            // Handle exception
            logger.error("Failed to process message: " + e.getMessage());
        }
    }
}

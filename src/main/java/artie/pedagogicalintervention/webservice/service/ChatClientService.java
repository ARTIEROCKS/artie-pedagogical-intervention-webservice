package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.dto.MessageDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import chat.ChatGrpc.ChatBlockingStub;
import chat.ChatOuterClass.ChatRequest;
import chat.ChatOuterClass.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatClientService {

    @GrpcClient("chatService")
    private ChatBlockingStub chatBlockingStub;

    @Value("${artie.webservices.conversations.queue}")
    private String queue;
    private final ObjectMapper objectMapper;
    private Logger logger;
    private Map<String, PedagogicalSoftwareData> mapUserContext;

    @Autowired
    private InterventionService interventionService;

    @Autowired
    public ChatClientService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getResponse(String userId, String contextId, String message, String prompt){
        return this.getResponse(userId, contextId, message, prompt, this.mapUserContext.get(userId));
    }

    public String getResponse(String userId, String contextId, String message, String prompt, PedagogicalSoftwareData psd) {
        ChatRequest request = ChatRequest.newBuilder()
                .setUserId(userId)
                .setContextId(contextId)
                .setMessage(message)
                .setPrompt(prompt)
                .build();

        if (!mapUserContext.containsKey(userId)){
            mapUserContext.put(userId, psd);
        }else{
            mapUserContext.replace(userId, psd);
        }

        ChatResponse response = chatBlockingStub.getResponse(request);
        return response.getReply();
    }

    @RabbitListener(queues = "${artie.webservices.conversations.queue}")
    public void receiveMessage(String messageContent) {
        try {
            // Parse the message content
            MessageDTO message = objectMapper.readValue(messageContent, MessageDTO.class);

            // Gets the answer from the chat
            String reply = getResponse(message.getUserId(), message.getContextId(), message.getMessage(), message.getPrompt());

            // Log or handle the reply as needed
            logger.info("Reply: " + reply);

            //Gets the pedagogical software data and builds the intervention with the reply from the Chat Client service
            PedagogicalSoftwareData psd = this.mapUserContext.get(message.getUserId());
            this.interventionService.buildAndSendIntervention(psd, reply);


        } catch (Exception e) {
            // Handle exception
            logger.error("Failed to process message: " + e.getMessage());
        }
    }
}

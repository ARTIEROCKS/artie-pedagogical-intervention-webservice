package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.dto.MessageDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class RabbitListenerService implements MessageListener {

    @Autowired
    private InterventionService interventionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatClientService chatClientService;
    private final Logger logger = LoggerFactory.getLogger(RabbitListenerService.class);


    public void onMessage(Message message) {
        try {
            String strMessage = new String(message.getBody());
            logger.info("Consuming Message - {}", strMessage);

            // Parse the message content
            MessageDTO messageDTO = this.objectMapper.readValue(strMessage, MessageDTO.class);

            // Gets the answer from the chat
            Map<String, PedagogicalSoftwareData> mapUserContext = interventionService.getMapUserContext();
            PedagogicalSoftwareData psd = mapUserContext != null ? mapUserContext.get(messageDTO.getUserId()) : null;
            String reply = chatClientService.getResponse(messageDTO.getUserId(), messageDTO.getContextId(), messageDTO.getMessage(), messageDTO.getPrompt());

            // Log or handle the reply as needed
            logger.info("Reply: " + reply);

            //Builds the intervention with the reply from the Chat Client service
            if (psd != null) {
                interventionService.buildAndSendIntervention(psd, reply);
            }else{
                logger.error("Pedagogical Software Data is null, so any intervention can be performed");
            }

        } catch (Exception e) {
            // Handle exception
            logger.error("Failed to process message: {}", e.getMessage());
        }
    }

}
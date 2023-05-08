package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.model.EmotionalStateMessage;
import artie.sensor.common.dto.SensorObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmotionalStateService {

    private final RabbitTemplate rabbitTemplate;
    @Value("${artie.webservices.emotional.queue}")
    private String queue;
    private final ObjectMapper mapper;

    @Autowired
    public EmotionalStateService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = new ObjectMapper();
    }

    public void sendEmotionalStateMessage(List<SensorObject> messages, String externalId) {

        // Declare the queue if it doesn't exist
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(this.queue, true, false, false, null);
            return null;
        });

        //Gets all the sensor objects and inserts them in the queue
        messages.forEach(d -> {
            String json = null;
            try {
                json = mapper.writeValueAsString(new EmotionalStateMessage(d, externalId));
                rabbitTemplate.convertAndSend(this.queue, json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
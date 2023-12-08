package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.dto.EmotionalStateDTO;
import artie.pedagogicalintervention.webservice.model.EmotionalStateMessage;
import artie.sensor.common.dto.SensorObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
public class EmotionalStateService {

    private final RabbitTemplate rabbitTemplate;
    @Value("${artie.webservices.emotional.queue}")
    private String queue;
    private final ObjectMapper mapper;

    @Value("${artie.api.key}")
    private String apiKey;
    private RestTemplate restTemplate;
    private HttpEntity<String> entity;

    @Value("${artie.webservices.emotional.url}")
    private String emotionalWebserviceUrl;

    private Logger logger;


    @Autowired
    public EmotionalStateService(RabbitTemplate rabbitTemplate, RestTemplateBuilder builder) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = new ObjectMapper();
        this.restTemplate = builder.build();
    }

    @PostConstruct
    public void setUp(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("apiKey", this.apiKey);
        this.entity = new HttpEntity<String>("parameters", headers);
        logger = LoggerFactory.getLogger(EmotionalStateService.class);
    }


    /**
     * Function to send the sensor information to the emotional state queue
     * @param messages
     * @param externalId
     */
    public void sendEmotionalStateMessage(List<SensorObject> messages, String externalId) {

        // Declare the queue if it doesn't exist
        logger.info("Sending emotional state messages to external id " + externalId);
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(this.queue, true, false, false, null);
            return null;
        });

        //Gets all the sensor objects and inserts them in the queue
        messages.forEach(d -> {
            String json = null;
            try {
                logger.trace("Sending emotional message to rabbit queue");
                json = mapper.writeValueAsString(new EmotionalStateMessage(d, externalId));
                logger.debug("Sending emotional message: " + json);
                rabbitTemplate.convertAndSend(this.queue, json);
            } catch (JsonProcessingException e) {
                logger.error("Error parsing JSON: " + e.getMessage());
            }
        });
    }

    /**
     * Function that receives the student ID and calls the emotional model prediction
     * @param studentId
     * @return
     */
    public EmotionalStateDTO predict(String studentId) {

        logger.info("Predicting emotional state for student id: " + studentId);
        EmotionalStateDTO emotionalState = null;

        try {

            //1- Calls for the webservice
            String wsResponse = restTemplate.getForObject(emotionalWebserviceUrl + "/predict?externalId=" + studentId, String.class);

            //2- Transforms the string into the object
            emotionalState = new ObjectMapper().readValue(wsResponse, EmotionalStateDTO.class);
            logger.trace("Emotional state dectected: " + emotionalState + " for student id: " + studentId);

        } catch (Exception e) {
            logger.error("Error when trying to predict emotional state: " + e.getMessage());
        }

        //3- Returns the boolean body object
        return emotionalState;
    }
}
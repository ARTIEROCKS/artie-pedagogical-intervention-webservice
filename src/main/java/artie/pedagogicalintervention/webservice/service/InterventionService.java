package artie.pedagogicalintervention.webservice.service;

import artie.generator.dto.bmle.BML;
import artie.generator.service.GeneratorService;
import artie.generator.service.GeneratorServiceImpl;
import artie.pedagogicalintervention.webservice.dto.EmotionalStateDTO;
import artie.pedagogicalintervention.webservice.dto.MessageDTO;
import artie.pedagogicalintervention.webservice.dto.PrologAnswerDTO;
import artie.pedagogicalintervention.webservice.dto.PrologQueryDTO;
import artie.pedagogicalintervention.webservice.model.LLMPrompt;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterventionService {

    @Value("${API_KEY}")
    private String apiKey;

    @Value("${artie.webservices.prolog.query.url}")
    private String interventionWebserviceUrl;
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;

    @Value("${artie.webservices.interventions.queue}")
    private String interventionsQueue;

    @Value("${artie.webservices.conversations.queue}")
    private String conversationsQueue;

    @Autowired
    private ObjectMapper objectMapper;
    private final GeneratorService generatorService = new GeneratorServiceImpl();

    @Autowired
    private EmotionalStateService emotionalStateService;

    @Autowired
    private PedagogicalSoftwareService pedagogicalSoftwareService;

    @Autowired
    private LLMPromptService LLMPromptService;
    private HttpEntity<String> entity;

    @Autowired
    private ChatClientService chatClientService;
    private Map<String, PedagogicalSoftwareData> mapUserContext;

    private Logger logger;

    @Autowired
    public InterventionService(RabbitTemplate rabbitTemplate, RestTemplateBuilder builder, AmqpAdmin amqpAdmin){
        this.restTemplate = builder.build();
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void setUp(){
        this.headers = new HttpHeaders();
        this.headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        this.headers.add("apiKey", this.apiKey);
        this.mapUserContext = new HashMap<>();
        logger = LoggerFactory.getLogger(InterventionService.class);

        // Declare the queue if it doesn't exist
        Queue interventionsQueue = new Queue(this.interventionsQueue, true);
        Queue conversationsQueue = new Queue(this.conversationsQueue, true);
        this.amqpAdmin.declareQueue(interventionsQueue);
        this.amqpAdmin.declareQueue(conversationsQueue);
    }

    /**
     * Function to send the intervention from the pedagogical software data id
     * @param id
     */
    public void buildAndSendInterventionByPedagogicalSoftwareDataId(String id) throws JsonProcessingException {
        logger.info("Building and sending the intervention by pedagogical software data id: " + id);
        PedagogicalSoftwareData psd = this.pedagogicalSoftwareService.findById(id);
        this.buildAndSendIntervention(psd, null);
    }

    /**
     * Function to send the intervention from the pedagogcial software data in json string
     * @param psd
     */
    public void buildAndSendIntervention(String psd) throws JsonProcessingException {

        PedagogicalSoftwareData pedagogicalSoftwareData = this.objectMapper.readValue(psd,
                PedagogicalSoftwareData.class);

        //We send the intervention if the student has requested help
        if(pedagogicalSoftwareData.isRequestHelp()) {
            this.buildAndSendIntervention(pedagogicalSoftwareData, null);
        }
    }

    /**
     * Function to build and send the intervention to the robot queue
     * @param pedagogicalSoftwareData
     * @param robotMessage
     * @throws JsonProcessingException
     */
    public void buildAndSendIntervention(PedagogicalSoftwareData pedagogicalSoftwareData, String robotMessage) throws JsonProcessingException {

        logger.info("Building and sending the intervention for id " + pedagogicalSoftwareData.getId());
        PrologQueryDTO prologQuery = PrologQueryDTO.builder()
                                        .institutionId(pedagogicalSoftwareData.getStudent().getInstitutionId())
                                        .build();

        //1.0 Creates the mapping for his/her last pedagogical software data
        String userId = pedagogicalSoftwareData.getStudent().getUserId();
        if (!mapUserContext.containsKey(userId)){
            mapUserContext.put(userId, pedagogicalSoftwareData);
        }else{
            mapUserContext.replace(userId, pedagogicalSoftwareData);
        }

        //1.1 Gets the emotional state of the student
        EmotionalStateDTO emotionalStateDTO = this.emotionalStateService.predict(userId);
        String emotionalState = "neutral";
        if (emotionalStateDTO != null && emotionalStateDTO.getEmotionalState() != null && !emotionalStateDTO.getEmotionalState().equals("NONE")){
            emotionalState = emotionalStateDTO.getEmotionalState().toLowerCase();
        }
        logger.trace("Emotional state: " + emotionalState + " for user id: " + userId);

        //1.2 Gets the eyes
        prologQuery.setQuery("pedagogicalIntervention(Eye,Tone,Speed,Gesture,Prompt," + emotionalState.toLowerCase() + ").");
        HttpEntity<PrologQueryDTO> request = new HttpEntity<>(prologQuery, headers);

        try {
            PrologAnswerDTO[][] answer = restTemplate.postForObject(interventionWebserviceUrl, request, PrologAnswerDTO[][].class);
            assert answer != null;
            String eyes = getValueFromPrologAnswer(answer, "Eye");
            logger.trace("Eyes: " + eyes + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.3 Gets the tone of the voice
            String toneOfVoice = getValueFromPrologAnswer(answer, "Tone");
            logger.trace("Tone of voice: " + toneOfVoice + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.4 Gets the voice speed
            String voiceSpeed = getValueFromPrologAnswer(answer, "Speed");
            logger.trace("Voice speed: " + voiceSpeed + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.5 Gets the gaze
            String gaze = getValueFromPrologAnswer(answer, "Gaze");
            logger.trace("Gaze: " + gaze + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.6 Gets the gesture
            String gesture = getValueFromPrologAnswer(answer, "Gesture");
            logger.trace("Gesture: " + gesture + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.7 Gets the posture
            String posture = "stand";

            //1.8 The LLM prompt is given by a key, so we have to first get the prompt from the db
            String prompt = "";
            String promptKey = getValueFromPrologAnswer(answer, "Prompt");
            List<LLMPrompt> LLMPromptList = this.LLMPromptService.findByInstitutionIdAndPromptKey(pedagogicalSoftwareData.getStudent().getInstitutionId(), promptKey);

            if (!LLMPromptList.isEmpty()) {
                prompt = LLMPromptList.get(0).getPrompt();
                logger.trace("Prompt: " + prompt + " for emotional state " + emotionalState + " and user id: " + userId);
            }

            //1.9 Creates the context and gets the message to be read by the robot, if it has not been obtained out of the function
            String contextId = pedagogicalSoftwareData.getStudent().getId() + "-" + pedagogicalSoftwareData.getExercise().getId();
            String sentence = robotMessage;

            if (sentence == null) {
                logger.trace("Sentence is null. Getting sentence from conversation service.");
                sentence = this.chatClientService.getResponse(pedagogicalSoftwareData.getStudent().getUserId(), contextId, "", prompt);
            }
            logger.trace("LLM Sentence: " + sentence);

            //2. Building the BMLe with the first sentence found
            BML bml = new BML(pedagogicalSoftwareData.getId(),
                    pedagogicalSoftwareData.getStudent().getUserId(),
                    posture, gaze, eyes, gesture, toneOfVoice, voiceSpeed, sentence);

            String bmle = generatorService.generateBMLE(bml);
            logger.trace("BMLE generated for user id " + userId + ": " + bmle);

            //3. Sends the BMLe to the queue message to let the robot process the messages
            // Declare the queue if it doesn't exist
            rabbitTemplate.execute(channel -> {
                channel.queueDeclare(this.interventionsQueue, true, false, false, null);
                return null;
            });
            rabbitTemplate.convertAndSend(this.interventionsQueue, bmle);
        }
        catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }

    /**
     * Function to get the value from the PROLOG answer
     * @param eyeSelectionAnswer
     * @param variableName
     * @return
     */
    public String getValueFromPrologAnswer(PrologAnswerDTO[][] eyeSelectionAnswer, String variableName) {

        String value = null;
        for (PrologAnswerDTO[] answerRow : eyeSelectionAnswer) {
            for (PrologAnswerDTO answer : answerRow) {
                if (answer.getVariable().equals(variableName)) {
                    value = answer.getValue();
                    break;
                }
            }
            if (value != null) {
                break;
            }
        }
        return value;
    }

    @RabbitListener(queues = "${artie.webservices.conversations.queue}")
    public void receiveMessage(String messageContent) {
        try {
            // Parse the message content
            MessageDTO message = objectMapper.readValue(messageContent, MessageDTO.class);

            // Gets the answer from the chat
            PedagogicalSoftwareData psd = this.mapUserContext.get(message.getUserId());
            String reply = this.chatClientService.getResponse(message.getUserId(), message.getContextId(), message.getMessage(), message.getPrompt());

            // Log or handle the reply as needed
            logger.info("Reply: " + reply);

            //Builds the intervention with the reply from the Chat Client service
            buildAndSendIntervention(psd, reply);

        } catch (Exception e) {
            // Handle exception
            logger.error("Failed to process message: " + e.getMessage());
        }
    }
}

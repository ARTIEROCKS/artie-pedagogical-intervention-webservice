package artie.pedagogicalintervention.webservice.service;

import artie.generator.dto.bmle.BML;
import artie.generator.service.GeneratorService;
import artie.generator.service.GeneratorServiceImpl;
import artie.pedagogicalintervention.webservice.dto.EmotionalStateDTO;
import artie.pedagogicalintervention.webservice.dto.PrologAnswerDTO;
import artie.pedagogicalintervention.webservice.dto.PrologQueryDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSentence;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
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
public class InterventionService {

    @Value("${API_KEY}")
    private String apiKey;

    @Value("${artie.webservices.prolog.query.url}")
    private String interventionWebserviceUrl;
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private final RabbitTemplate rabbitTemplate;

    @Value("${artie.webservices.interventions.queue}")
    private String queue;

    @Autowired
    private ObjectMapper objectMapper;
    private final GeneratorService generatorService = new GeneratorServiceImpl();

    @Autowired
    private EmotionalStateService emotionalStateService;

    @Autowired
    private PedagogicalSoftwareService pedagogicalSoftwareService;

    @Autowired
    private PedagogicalSentenceService pedagogicalSentenceService;
    private HttpEntity<String> entity;

    private Logger logger;

    @Autowired
    public InterventionService(RabbitTemplate rabbitTemplate, RestTemplateBuilder builder){
        this.restTemplate = builder.build();
        this.rabbitTemplate = rabbitTemplate;
        logger = LoggerFactory.getLogger(InterventionService.class);
    }

    @PostConstruct
    public void setUp(){
        this.headers = new HttpHeaders();
        this.headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        this.headers.add("apiKey", this.apiKey);
        logger = LoggerFactory.getLogger(InterventionService.class);
    }

    /**
     * Function to send the intervention from the pedagogical software data id
     * @param id
     */
    public void buildAndSendInterventionByPedagogicalSoftwareDataId(String id) throws JsonProcessingException {
        logger.info("Building and sending the intervention by pedagogical software data id: " + id);
        PedagogicalSoftwareData psd = this.pedagogicalSoftwareService.findById(id);
        this.buildAndSendIntervention(psd);
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
            this.buildAndSendIntervention(pedagogicalSoftwareData);
        }
    }

    /**
     * Function to build and send the intervention to the robot queue
     * @param pedagogicalSoftwareData
     * @throws JsonProcessingException
     */
    public void buildAndSendIntervention(PedagogicalSoftwareData pedagogicalSoftwareData) throws JsonProcessingException {

        logger.info("Building and sending the intervention for id " + pedagogicalSoftwareData.getId());
        PrologQueryDTO prologQuery = PrologQueryDTO.builder()
                                        .institutionId(pedagogicalSoftwareData.getStudent().getInstitutionId())
                                        .build();

        //1.1 Gets the emotional state of the student
        String userId = pedagogicalSoftwareData.getStudent().getUserId();
        EmotionalStateDTO emotionalStateDTO = this.emotionalStateService.predict(userId);

        String emotionalState = "neutral";
        if (emotionalStateDTO != null && emotionalStateDTO.getEmotionalState() != null && !emotionalStateDTO.getEmotionalState().equals("NONE")){
            emotionalState = emotionalStateDTO.getEmotionalState().toLowerCase();
        }
        logger.trace("Emotional state: " + emotionalState + " for user id: " + userId);

        //1.2 Gets the eyes
        prologQuery.setQuery("pedagogicalIntervention(Eye,Tone,Speed,Gesture,Sentence," + emotionalState.toLowerCase() + ").");
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
            String gaze = pedagogicalSoftwareData.getStudent().getUserId();
            logger.trace("Gaze: " + gaze + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.6 Gets the gesture
            String gesture = getValueFromPrologAnswer(answer, "Gesture");
            logger.trace("Gesture: " + gesture + " for emotional state " + emotionalState + " and user id: " + userId);

            //1.7 Gets the posture
            String posture = "stand";

            //1.8 The text is given by a key, so we have to first get the sentence from the db
            String sentenceKey = getValueFromPrologAnswer(answer, "Sentence");
            List<PedagogicalSentence> pedagogicalSentenceList = this.pedagogicalSentenceService.findByInstitutionIdAndSentenceKey(pedagogicalSoftwareData.getStudent().getInstitutionId(), sentenceKey);
            String sentence = "";
            if (!pedagogicalSentenceList.isEmpty()) {
                sentence = pedagogicalSentenceList.get(0).getSentence();
                logger.trace("Sentence: " + sentence + " for emotional state " + emotionalState + " and user id: " + userId);
            }

            //2. Building the BMLe with the first sentence found
            BML bml = new BML(pedagogicalSoftwareData.getId(),
                    pedagogicalSoftwareData.getStudent().getUserId(),
                    posture, gaze, eyes, gesture, toneOfVoice, voiceSpeed, sentence);

            String bmle = generatorService.generateBMLE(bml);
            logger.trace("BMLE generated for user id " + userId + ": " + bmle);

            //3. Sends the BMLe to the queue message to let the robot process the messages
            // Declare the queue if it doesn't exist
            rabbitTemplate.execute(channel -> {
                channel.queueDeclare(this.queue, true, false, false, null);
                return null;
            });
            rabbitTemplate.convertAndSend(this.queue, bmle);
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
}

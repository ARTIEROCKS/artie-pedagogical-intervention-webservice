package artie.pedagogicalintervention.webservice.service;

import artie.generator.dto.bmle.BML;
import artie.generator.service.GeneratorService;
import artie.generator.service.GeneratorServiceImpl;
import artie.pedagogicalintervention.webservice.dto.PrologAnswerDTO;
import artie.pedagogicalintervention.webservice.dto.PrologQueryDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private HttpEntity<String> entity;

    @Autowired
    public InterventionService(RabbitTemplate rabbitTemplate, RestTemplateBuilder builder){
        this.restTemplate = builder.build();
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void setUp(){
        this.headers = new HttpHeaders();
        this.headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        this.headers.add("apiKey", this.apiKey);
    }

    /**
     * Function to send the intervention from the pedagogical software data id
     * @param id
     */
    public void buildAndSendInterventionByPedagogicalSoftwareDataId(String id) throws JsonProcessingException {
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

        PrologQueryDTO prologQuery = PrologQueryDTO.builder()
                                        .institutionId(pedagogicalSoftwareData.getStudent().getInstitutionId())
                                        .build();

        //1.1 Gets the emotional state of the student
        String emotionalState = this.emotionalStateService.predict(pedagogicalSoftwareData.getStudent().getUserId()).getEmotionalState();
        if (emotionalState == null || emotionalState == "NONE"){
            emotionalState = "neutral";
        }

        //1.2 Gets the eyes
        prologQuery.setQuery("pedagogicalIntervention(Eye,Tone,Speed,Gesture,Sentence," + emotionalState.toLowerCase() + ").");
        HttpEntity<PrologQueryDTO> request = new HttpEntity<>(prologQuery, headers);
        PrologAnswerDTO[][] answer = restTemplate.postForObject(interventionWebserviceUrl,request, PrologAnswerDTO[][].class);
        assert answer != null;
        String eyes = getValueFromPrologAnswer(answer, "Eye");

        //1.3 Gets the tone of the voice
        String toneOfVoice = getValueFromPrologAnswer(answer, "Tone");

        //1.4 Gets the voice speed
        String voiceSpeed = getValueFromPrologAnswer(answer, "Speed");

        //1.5 Gets the gaze
        String gaze = pedagogicalSoftwareData.getStudent().getUserId();
        
        //1.6 Gets the gesture
        String gesture = getValueFromPrologAnswer(answer, "Gesture");

        //1.7 Gets the posture
        String posture = "stand";

        //1.8 Gets the text to say
        String text = getValueFromPrologAnswer(answer, "Sentence");

        //2. Building the BMLe
        BML bml = new BML(pedagogicalSoftwareData.getId(),
                          pedagogicalSoftwareData.getStudent().getUserId(),
                          posture, gaze, eyes, gesture, toneOfVoice, voiceSpeed, text);

        String bmle = generatorService.generateBMLE(bml);

        //3. Sends the BMLe to the queue message to let the robot process the messages
        // Declare the queue if it doesn't exist
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(this.queue, true, false, false, null);
            return null;
        });
        rabbitTemplate.convertAndSend(this.queue, bmle);
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

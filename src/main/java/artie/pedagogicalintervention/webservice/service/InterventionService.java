package artie.pedagogicalintervention.webservice.service;

import artie.generator.dto.bmle.BML;
import artie.generator.service.GeneratorService;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private HttpEntity<String> entity;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    public InterventionService(RestTemplateBuilder builder){this.restTemplate = builder.build();}

    @PostConstruct
    public void setUp(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("apiKey", this.apiKey);
        this.entity = new HttpEntity<String>("parameters", headers);
    }

    public void buildIntervention(PedagogicalSoftwareData pedagogicalSoftwareData) throws JsonProcessingException {

        //PrologQueryDTO prologQuery = PrologQueryDTO.builder()

        //1.1 Gets the emotional state of the student
        String emotionalState = "HAPPY";
        //1.2 Gets the eyes
        String eyes = "";//restTemplate.postForObject(interventionWebserviceUrl, softwareData, String.class);;
        //1.3 Gets the tone of the voice
        String toneOfVoice = "high";
        //1.4 Gets the voice speed
        String voiceSpeed = "low";
        //1.5 Gets the gaze
        String gaze = "testGaze";
        //1.6 Gets the gesture
        String gesture = "testGesture";
        //1.7 Gets the posture
        String posture = "testPosture";
        //1.8 Gets the text to say
        String text = "Hola!";

        //2. Building the BMLe
        BML bml = new BML(pedagogicalSoftwareData.getId(),
                          pedagogicalSoftwareData.getStudent().getUserId(),
                          posture, gaze, eyes, gesture, toneOfVoice, voiceSpeed, text);
        String bmle = generatorService.generateBMLE(bml);

        //3. Sends the BMLe to the queue message to let the robot process the messages
    }
}

package artie.pedagogicalintervention.webservice.service;

import artie.common.web.dto.SoftwareData;
import artie.pedagogicalintervention.webservice.dto.HelpModelDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HelpModelService {

    @Value("${artie.api.key}")
    private String apiKey;
    private RestTemplate restTemplate;
    private HttpEntity<String> entity;

    @Value("${artie.webservices.help.url}")
    private String helpWebserviceUrl;

    private Logger logger;

    @Autowired
    public HelpModelService(RestTemplateBuilder builder){this.restTemplate = builder.build();}
    public HelpModelService(){logger = LoggerFactory.getLogger(HelpModelService.class);}

    @PostConstruct
    public void setUp(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("apiKey", this.apiKey);
        this.entity = new HttpEntity<String>("parameters", headers);
        logger = LoggerFactory.getLogger(HelpModelService.class);
    }

    /**
     * Calls help-model service with the provided data and returns the parsed HelpModelDTO.
     * It sends a non-empty array as required by the Python service.
     */
    public HelpModelDTO predict(PedagogicalSoftwareData pedagogicalSoftwareData) {
        logger.info("Predicting if the student " + (pedagogicalSoftwareData.getStudent() != null ? pedagogicalSoftwareData.getStudent().getId() : "null") + " needs help");

        try {
            // 1- Build the payload from the pedagogical software data
            SoftwareData softwareData = pedagogicalSoftwareData.toDTO();
            List<SoftwareData> payload = Collections.singletonList(softwareData);

            // Prepare mapper for logging and parsing
            ObjectMapper mapper = new ObjectMapper();

            // Log request payload (JSON array with one interaction)
            try {
                String payloadJson = mapper.writeValueAsString(payload);
                logger.debug("Prediction service request payload: " + payloadJson);
            } catch (Exception ex) {
                logger.warn("Could not serialize prediction payload for logging: " + ex.getMessage());
            }

            // 2- Call the webservice (expects an array)
            String wsResponse = restTemplate.postForObject(helpWebserviceUrl + "/predict", payload, String.class);
            logger.info("Prediction service response for student " + (pedagogicalSoftwareData.getStudent() != null ? pedagogicalSoftwareData.getStudent().getId() : "null") + ": " + wsResponse);

            // 3- Parse JSON and map body to HelpModelDTO
            JsonNode root = mapper.readTree(wsResponse);
            JsonNode bodyNode = root.path("body");
            if (bodyNode.isMissingNode() || bodyNode.isNull()) {
                logger.error("Help model response has no body node");
                return null;
            }
            HelpModelDTO dto = mapper.treeToValue(bodyNode, HelpModelDTO.class);
            logger.info("Help need predicted: " + (dto != null ? dto.getHelpNeeded() : null));
            return dto;
        } catch (Exception e) {
            logger.error("Error during the help need prediction: " + e.getMessage());
            return null;
        }
    }
}

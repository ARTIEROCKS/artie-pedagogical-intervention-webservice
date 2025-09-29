package artie.pedagogicalintervention.webservice.service;

import artie.common.web.dto.Response;
import artie.common.web.dto.SoftwareData;
import artie.common.web.enums.ResponseCodeEnum;
import artie.pedagogicalintervention.webservice.dto.HelpModelDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Objects;

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
     * Function that receives the pedagogical software data and calls the help model prediction
     * @param pedagogicalSoftwareData
     * @return
     */
    public boolean predict(PedagogicalSoftwareData pedagogicalSoftwareData) {

        boolean result = false;
        logger.info("Predicting if the student " + pedagogicalSoftwareData.getStudent().getId() + " needs help");

        try {
            //1- Gets the parent of the pedagogical software data, that will be sent
            SoftwareData softwareData = pedagogicalSoftwareData.toDTO();

            //2- Calls for the webservice
            String wsResponse = restTemplate.postForObject(helpWebserviceUrl + "/predict", softwareData, String.class);
            logger.info("Result for the prediction for the student " + pedagogicalSoftwareData.getStudent().getId() + ": " + wsResponse);

            ObjectMapper mapper = new ObjectMapper();

            // First, try to parse new/any JSON format directly
            boolean newFormatHelp = false;
            try {
                JsonNode root = mapper.readTree(wsResponse);
                JsonNode topMessage = root.path("message");
                boolean topLevelOk = topMessage.isMissingNode() || !Objects.equals(topMessage.asText(), ResponseCodeEnum.ERROR.toString());

                JsonNode bodyNode = root.path("body");
                JsonNode helpNode = bodyNode.path("help_needed");
                if (helpNode.isBoolean()) {
                    newFormatHelp = helpNode.asBoolean() && topLevelOk;
                } else if (helpNode.isNumber()) {
                    newFormatHelp = helpNode.intValue() == 1 && topLevelOk;
                } else if (bodyNode.isObject()) {
                    // Map entire body to DTO and read helpNeeded/need_help via alias
                    HelpModelDTO dto = mapper.treeToValue(bodyNode, HelpModelDTO.class);
                    newFormatHelp = dto != null && Boolean.TRUE.equals(dto.getHelpNeeded()) && topLevelOk;
                } else if (bodyNode.isBoolean()) {
                    // legacy very-compact format: body is directly a boolean
                    newFormatHelp = bodyNode.asBoolean() && topLevelOk;
                } else if (bodyNode.isNumber()) {
                    // legacy compact numeric format: body is directly 1/0
                    newFormatHelp = bodyNode.intValue() == 1 && topLevelOk;
                }
            } catch (Exception ignored) {
                // ignore tree parsing issues; legacy parsing might work
            }

            // Then, try legacy Response structure
            boolean legacyHelp = false;
            try {
                Response response = mapper.readValue(wsResponse, Response.class);
                if(response != null && response.getBody() != null && Objects.equals(response.getBody().getMessage(), ResponseCodeEnum.ERROR.toString())){
                    System.out.println((String)response.getBody().getObject());
                    logger.error("Error during the help need prediction: " + (String)response.getBody().getObject());
                }
                boolean legacyOk = response != null && response.getBody() != null && !Objects.equals(response.getBody().getMessage(), ResponseCodeEnum.ERROR.toString());
                if (legacyOk && response.getBody().getObject() != null) {
                    Object obj = response.getBody().getObject();
                    if (obj instanceof Number) {
                        legacyHelp = ((Number) obj).intValue() == 1;
                    } else if (obj instanceof Boolean) {
                        legacyHelp = (Boolean) obj;
                    }
                }
            } catch (Exception ignored) {
                // ignore legacy parsing issues
            }

            result = legacyHelp || newFormatHelp;
            logger.info("Help need prediction: " + result + " for the student id: " + pedagogicalSoftwareData.getStudent().getId());

        } catch (Exception e) {
            logger.error("Error during the help need prediction: " + e.getMessage());
        }

        //3- Returns the boolean body object
        return result;
    }

}

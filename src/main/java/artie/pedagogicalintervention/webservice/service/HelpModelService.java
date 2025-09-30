package artie.pedagogicalintervention.webservice.service;

import artie.common.web.dto.SoftwareData;
import artie.pedagogicalintervention.webservice.dto.HelpModelDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;
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
import java.util.ArrayList;
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
    @Autowired(required = false)
    private PedagogicalSoftwareDataRepository pedagogicalSoftwareDataRepository;

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
            // Prepare mapper for logging and parsing
            ObjectMapper mapper = new ObjectMapper();

            // Build payload: optionally include recent history, and always include current interaction
            List<SoftwareData> finalPayload = new ArrayList<>();
            try {
                if (pedagogicalSoftwareDataRepository != null &&
                    pedagogicalSoftwareData != null &&
                    pedagogicalSoftwareData.getStudent() != null &&
                    pedagogicalSoftwareData.getStudent().getId() != null &&
                    pedagogicalSoftwareData.getExercise() != null &&
                    pedagogicalSoftwareData.getExercise().getId() != null &&
                    pedagogicalSoftwareData.getLastLogin() != null) {

                    List<PedagogicalSoftwareData> pedagogicalSoftwareDataList =
                            pedagogicalSoftwareDataRepository.findByStudent_IdAndExerciseIdAndLastLogin(
                                            pedagogicalSoftwareData.getStudent().getId(),
                                            pedagogicalSoftwareData.getExercise().getId(),
                                            pedagogicalSoftwareData.getLastLogin())
                                    .stream()
                                    .sorted(java.util.Comparator.comparing(PedagogicalSoftwareData::getDateTime).reversed())
                                    .toList();

                    int firstIndex = -1;
                    for (int i = 0; i < pedagogicalSoftwareDataList.size(); i++) {
                        PedagogicalSoftwareData psd = pedagogicalSoftwareDataList.get(i);
                        boolean matches = psd.isRequestHelp() || psd.isPredictedNeedHelp() || psd.isAnsweredNeedHelp();
                        if (matches) { firstIndex = i; break; }
                    }
                    if (firstIndex >= 0) {
                        pedagogicalSoftwareDataList = pedagogicalSoftwareDataList.subList(0, firstIndex);
                    }

                    pedagogicalSoftwareDataList = pedagogicalSoftwareDataList.stream()
                            .sorted(java.util.Comparator.comparing(PedagogicalSoftwareData::getDateTime))
                            .toList();

                    pedagogicalSoftwareDataList.forEach(psd -> finalPayload.add(psd.toDTO()));
                }
            } catch (Exception historyEx) {
                logger.debug("Skipping history payload build due to: {}", historyEx.getMessage());
            }

            // Always add current interaction
            if (pedagogicalSoftwareData != null) {
                finalPayload.add(pedagogicalSoftwareData.toDTO());
            }

            // Log request payload (JSON array)
            try {
                String payloadJson = mapper.writeValueAsString(finalPayload);
                logger.debug("Prediction service request payload: {}", payloadJson);
            } catch (Exception ex) {
                logger.warn("Could not serialize prediction payload for logging: {}", ex.getMessage());
            }

            // Call the webservice (expects an array)
            String wsResponse = restTemplate.postForObject(helpWebserviceUrl + "/predict", finalPayload, String.class);
            logger.info("Prediction service response for student " + (pedagogicalSoftwareData.getStudent() != null ? pedagogicalSoftwareData.getStudent().getId() : "null") + ": " + wsResponse);

            // Parse JSON and map body to HelpModelDTO
            JsonNode root = mapper.readTree(wsResponse);
            JsonNode bodyNode = root.path("body");
            if (bodyNode.isMissingNode() || bodyNode.isNull()) {
                logger.error("Help model response has no body node");
                return null;
            }

            HelpModelDTO dto;
            try {
                dto = mapper.treeToValue(bodyNode, HelpModelDTO.class);
            } catch (Exception parseEx) {
                logger.warn("Direct DTO mapping failed, applying manual mapping: " + parseEx.getMessage());
                dto = manualMapBody(bodyNode);
            }

            logger.info("Help need predicted: " + (dto != null ? dto.getHelpNeeded() : null));
            return dto;
        } catch (Exception e) {
            logger.error("Error during the help need prediction: " + e.getMessage());
            return null;
        }
    }

    private HelpModelDTO manualMapBody(JsonNode bodyNode) {
        if (bodyNode == null || bodyNode.isNull()) return null;
        HelpModelDTO dto = new HelpModelDTO();
        if (bodyNode.has("threshold") && !bodyNode.get("threshold").isNull()) {
            dto.setThreshold(bodyNode.get("threshold").asDouble());
        }
        JsonNode helpNode = bodyNode.has("help_needed") ? bodyNode.get("help_needed") : bodyNode.get("need_help");
        if (helpNode != null && !helpNode.isNull()) {
            dto.setHelpNeeded(helpNode.asBoolean());
        }
        if (bodyNode.has("last_probability") && !bodyNode.get("last_probability").isNull()) {
            dto.setLastProbability(bodyNode.get("last_probability").asDouble());
        }
        if (bodyNode.has("sequence_probabilities") && bodyNode.get("sequence_probabilities").isArray()) {
            List<Double> seq = new ArrayList<>();
            for (JsonNode n : bodyNode.get("sequence_probabilities")) {
                if (n != null && !n.isNull()) seq.add(n.asDouble());
            }
            dto.setSequenceProbabilities(seq);
        }
        if (bodyNode.has("attention") && bodyNode.get("attention").isObject()) {
            JsonNode att = bodyNode.get("attention");
            HelpModelDTO.Attention a = new HelpModelDTO.Attention();
            if (att.has("available")) a.setAvailable(att.get("available").asBoolean());
            if (att.has("seq_len")) a.setSeqLen(att.get("seq_len").asInt());
            if (att.has("top_k") && att.get("top_k").isArray()) {
                List<HelpModelDTO.TopK> topK = new ArrayList<>();
                for (JsonNode k : att.get("top_k")) {
                    if (k != null && k.isObject()) {
                        HelpModelDTO.TopK t = new HelpModelDTO.TopK();
                        if (k.has("t")) t.setT(k.get("t").asInt());
                        if (k.has("w")) t.setW(k.get("w").asDouble());
                        topK.add(t);
                    }
                }
                a.setTopK(topK);
            }
            dto.setAttention(a);
        }
        return dto;
    }
}

package artie.pedagogicalintervention.webservice.service;

import artie.common.web.dto.Response;
import artie.common.web.dto.ResponseBody;
import artie.common.web.dto.SoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class HelpModelService {

    @Value("${API_KEY}")
    private String apiKey;
    private RestTemplate restTemplate;
    private HttpEntity<String> entity;

    @Value("${artie.webservices.help.url}")
    private String helpWebserviceUrl;

    @Autowired
    public HelpModelService(RestTemplateBuilder builder){this.restTemplate = builder.build();}
    public HelpModelService(){}

    @PostConstruct
    public void setUp(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("apiKey", this.apiKey);
        this.entity = new HttpEntity<String>("parameters", headers);
    }

    /**
     * Function that receives the pedagogical software data and calls the help model prediction
     * @param pedagogicalSoftwareData
     * @return
     */
    public boolean predict(PedagogicalSoftwareData pedagogicalSoftwareData) {

        //1- Gets the parent of the pedagogical software data, that will be sent
        SoftwareData softwareData = pedagogicalSoftwareData.toDTO();

        //2- Calls for the webservice
        String wsResponse = restTemplate.postForObject(helpWebserviceUrl + "/predict", softwareData, String.class);

        //3- Transforms the string into the object
        Response response = null;
        try {
            response = new ObjectMapper().readValue(wsResponse, Response.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //3- Returns the boolean body object
        return response != null ? ((int)response.getBody().getObject() == 1 ? true : false ): false;
    }

}

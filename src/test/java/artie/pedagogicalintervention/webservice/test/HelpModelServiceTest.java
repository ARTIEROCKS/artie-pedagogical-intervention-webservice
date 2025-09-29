package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.dto.StudentDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.service.HelpModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpModelServiceTest {

    private HelpModelService helpModelService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        helpModelService = new HelpModelService();
        restTemplate = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(helpModelService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(helpModelService, "helpWebserviceUrl", "http://localhost:9999");
    }

    private PedagogicalSoftwareData buildMinimalPsd() {
        PedagogicalSoftwareData psd = new PedagogicalSoftwareData();
        StudentDTO student = new StudentDTO(
                "s1", "Name", "Last", "1234",
                0, 0, 10, 0, 0,
                false, false, false,
                "inst", "user"
        );
        psd.setStudent(student);
        return psd;
    }

    @Test
    void predict_setsExtendedFields_fromBody() {
        String json = "{\n" +
                "  \"message\": \"OK\",\n" +
                "  \"body\": {\n" +
                "    \"threshold\": 0.5,\n" +
                "    \"help_needed\": true,\n" +
                "    \"last_probability\": 0.75,\n" +
                "    \"sequence_probabilities\": [0.75, 0.6],\n" +
                "    \"attention\": {\n" +
                "      \"available\": true,\n" +
                "      \"top_k\": [{\"t\": 0, \"w\": 1.0}, {\"t\": 1, \"w\": 0.5}],\n" +
                "      \"seq_len\": 2\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(json);

        PedagogicalSoftwareData psd = buildMinimalPsd();
        boolean result = helpModelService.predict(psd);

        assertTrue(result, "help_needed should be true");
        assertEquals(0.5, psd.getPredictedNeededHelpThreshold());
        assertEquals(0.75, psd.getPredictedNeededHelpProbability());
        List<Double> seq = psd.getPredictedNeededHelpSequenceProbabilities();
        assertNotNull(seq);
        assertEquals(2, seq.size());
        assertEquals(0.75, seq.get(0));
        assertEquals(0.6, seq.get(1));
        assertNotNull(psd.getPredictedNeededHelpAttention());
        assertEquals(true, psd.getPredictedNeededHelpAttention().getAvailable());
        assertEquals(2, psd.getPredictedNeededHelpAttention().getSeqLen());
        assertNotNull(psd.getPredictedNeededHelpAttention().getTopK());
        assertEquals(2, psd.getPredictedNeededHelpAttention().getTopK().size());
        assertEquals(0, psd.getPredictedNeededHelpAttention().getTopK().get(0).getT());
        assertEquals(1.0, psd.getPredictedNeededHelpAttention().getTopK().get(0).getW());
        assertEquals(1, psd.getPredictedNeededHelpAttention().getTopK().get(1).getT());
        assertEquals(0.5, psd.getPredictedNeededHelpAttention().getTopK().get(1).getW());
    }
}

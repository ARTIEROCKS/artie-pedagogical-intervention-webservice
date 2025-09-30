package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.dto.HelpModelDTO;
import artie.pedagogicalintervention.webservice.dto.StudentDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.service.HelpModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
    void predict_returnsDto_andParsesBodyFields() {
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
        Mockito.when(restTemplate.postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(String.class)))
                .thenReturn(json);

        PedagogicalSoftwareData psd = buildMinimalPsd();
        HelpModelDTO dto = helpModelService.predict(psd);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(0.5, dto.getThreshold());
        assertTrue(Boolean.TRUE.equals(dto.getHelpNeeded()), "help_needed should be true");
        assertEquals(0.75, dto.getLastProbability());
        List<Double> seq = dto.getSequenceProbabilities();
        assertNotNull(seq, "sequence_probabilities should not be null");
        assertEquals(2, seq.size());
        assertEquals(0.75, seq.get(0));
        assertEquals(0.6, seq.get(1));
        assertNotNull(dto.getAttention(), "attention should not be null");
        assertEquals(true, dto.getAttention().getAvailable());
        assertEquals(2, dto.getAttention().getSeqLen());
        assertNotNull(dto.getAttention().getTopK());
        assertEquals(2, dto.getAttention().getTopK().size());
        assertEquals(0, dto.getAttention().getTopK().get(0).getT());
        assertEquals(1.0, dto.getAttention().getTopK().get(0).getW());
        assertEquals(1, dto.getAttention().getTopK().get(1).getT());
        assertEquals(0.5, dto.getAttention().getTopK().get(1).getW());
    }
}

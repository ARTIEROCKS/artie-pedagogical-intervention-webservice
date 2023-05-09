package artie.pedagogicalintervention.webservice.test;
import artie.pedagogicalintervention.webservice.dto.PrologAnswerDTO;
import artie.pedagogicalintervention.webservice.service.InterventionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterventionServiceTest {

    private InterventionService interventionService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper();
        interventionService = new InterventionService(null, new RestTemplateBuilder());
    }

    @Test
    void getValueFromPrologAnswerTest() throws JsonProcessingException {

        String json = "[\n" +
                "    [\n" +
                "        {\n" +
                "            \"variable\": \"X\",\n" +
                "            \"value\": \"ask\"\n" +
                "        }\n" +
                "    ]\n" +
                "]";

        PrologAnswerDTO[][] object = mapper.readValue(json, PrologAnswerDTO[][].class);
        String result = interventionService.getValueFromPrologAnswer(object, "X");
        assertEquals("ask", result);
    }

    @Test
    void getValuesFromPrologAnswerTest() throws JsonProcessingException {

        String json = "[\n" +
                "    [\n" +
                "        {\n" +
                "            \"variable\": \"Eye\",\n" +
                "            \"value\": \"green\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"variable\": \"Tone\",\n" +
                "            \"value\": \"high\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"variable\": \"Speed\",\n" +
                "            \"value\": \"high\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"variable\": \"Gesture\",\n" +
                "            \"value\": \"ask\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"variable\": \"Sentence\",\n" +
                "            \"value\": \"ask_scratch_help\"\n" +
                "        }\n" +
                "    ]\n" +
                "]";

        PrologAnswerDTO[][] object = mapper.readValue(json, PrologAnswerDTO[][].class);
        String eye = interventionService.getValueFromPrologAnswer(object, "Eye");
        String tone = interventionService.getValueFromPrologAnswer(object, "Tone");
        String speed = interventionService.getValueFromPrologAnswer(object, "Speed");
        String gesture = interventionService.getValueFromPrologAnswer(object, "Gesture");
        String sentence = interventionService.getValueFromPrologAnswer(object, "Sentence");

        assertEquals("green", eye);
        assertEquals("high", tone);
        assertEquals("high", speed);
        assertEquals("ask", gesture);
        assertEquals("ask_scratch_help", sentence);
    }
}

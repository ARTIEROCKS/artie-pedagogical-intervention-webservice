package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.dto.HelpModelDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareSolutionRepository;
import artie.pedagogicalintervention.webservice.service.DistanceCalculationService;
import artie.pedagogicalintervention.webservice.service.HelpModelService;
import artie.pedagogicalintervention.webservice.service.InterventionService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedagogicalSoftwareServiceAddHelpDtoTest {

    private PedagogicalSoftwareService service;
    private HelpModelService helpModelService;
    private PedagogicalSoftwareDataRepository dataRepository;
    private PedagogicalSoftwareSolutionRepository solutionRepository;
    private DistanceCalculationService distanceService;
    private InterventionService interventionService;

    @BeforeEach
    void setup() {
        service = new PedagogicalSoftwareService(new RestTemplateBuilder());
        helpModelService = mock(HelpModelService.class);
        dataRepository = mock(PedagogicalSoftwareDataRepository.class);
        solutionRepository = mock(PedagogicalSoftwareSolutionRepository.class);
        distanceService = mock(DistanceCalculationService.class);
        interventionService = mock(InterventionService.class);

        // Wire dependencies
        org.springframework.test.util.ReflectionTestUtils.setField(service, "helpModelService", helpModelService);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "pedagogicalSoftwareDataRepository", dataRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "pedagogicalSoftwareSolutionRepository", solutionRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "distanceCalculationService", distanceService);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "interventionService", interventionService);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());

        // repository.save should return an object with id
        when(dataRepository.save(Mockito.any(PedagogicalSoftwareData.class))).thenAnswer(inv -> {
            PedagogicalSoftwareData psd = inv.getArgument(0);
            psd.setId("saved-id");
            return psd;
        });
    }

    @Test
    void add_string_applies_helpDto_fields() {
        // Arrange DTO
        HelpModelDTO.Attention att = new HelpModelDTO.Attention(true,
                Arrays.asList(new HelpModelDTO.TopK(0, 1.0), new HelpModelDTO.TopK(1, 0.5)), 2);
        HelpModelDTO dto = new HelpModelDTO(0.5, true, 0.75, Arrays.asList(0.75, 0.6), att);
        when(helpModelService.predict(Mockito.any(PedagogicalSoftwareData.class))).thenReturn(dto);

        // Minimal JSON: only student fields required by the model
        String json = "{\n" +
                "  \"student\": {\n" +
                "    \"id\": \"s1\",\n" +
                "    \"name\": \"Name\",\n" +
                "    \"lastName\": \"Last\",\n" +
                "    \"studentNumber\": \"123\",\n" +
                "    \"gender\": 0,\n" +
                "    \"motherTongue\": 0,\n" +
                "    \"age\": 10,\n" +
                "    \"competence\": 0,\n" +
                "    \"motivation\": 0,\n" +
                "    \"recordFace\": false,\n" +
                "    \"recordInteractions\": false,\n" +
                "    \"interactsWithRobot\": false,\n" +
                "    \"institutionId\": \"inst\",\n" +
                "    \"userId\": \"user\"\n" +
                "  }\n" +
                "}";

        // Act
        String response = service.add(json);
        assertNotNull(response);

        // Capture saved object
        ArgumentCaptor<PedagogicalSoftwareData> captor = ArgumentCaptor.forClass(PedagogicalSoftwareData.class);
        verify(dataRepository, atLeastOnce()).save(captor.capture());
        PedagogicalSoftwareData saved = captor.getValue();

        // Assert fields were applied
        assertTrue(saved.isPredictedNeedHelp());
        assertEquals(0.5, saved.getPredictedNeededHelpThreshold());
        assertEquals(0.75, saved.getPredictedNeededHelpProbability());
        assertNotNull(saved.getPredictedNeededHelpSequenceProbabilities());
        assertEquals(2, saved.getPredictedNeededHelpSequenceProbabilities().size());
        assertNotNull(saved.getPredictedNeededHelpAttention());
        assertEquals(true, saved.getPredictedNeededHelpAttention().getAvailable());
        assertEquals(2, saved.getPredictedNeededHelpAttention().getSeqLen());
        assertNotNull(saved.getPredictedNeededHelpAttention().getTopK());
        assertEquals(2, saved.getPredictedNeededHelpAttention().getTopK().size());
        assertEquals(0, saved.getPredictedNeededHelpAttention().getTopK().get(0).getT());
        assertEquals(1.0, saved.getPredictedNeededHelpAttention().getTopK().get(0).getW());
    }
}


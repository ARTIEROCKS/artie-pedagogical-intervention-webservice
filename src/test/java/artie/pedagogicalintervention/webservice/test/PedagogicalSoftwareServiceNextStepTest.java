package artie.pedagogicalintervention.webservice.test;

import artie.common.web.dto.NextStepHint;
import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PedagogicalSoftwareServiceNextStepTest {

    private PedagogicalSoftwareService pedagogicalSoftwareService;

    @BeforeEach
    void setUp() throws Exception {
        pedagogicalSoftwareService = new PedagogicalSoftwareService();
    }

    @Test
    void familyNextStepsCalculationTest() {

        //Setup
        PedagogicalSoftwareElementDTO origin1 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1","family1", null, null));
        PedagogicalSoftwareElementDTO origin2 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element2", "family2", null, null));
        PedagogicalSoftwareElementDTO origin3 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element3", "family3", null, null));
        PedagogicalSoftwareElementDTO origin4 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element4", "family4", null, null));

        PedagogicalSoftwareElementDTO aim1 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1","family1", null, null));
        PedagogicalSoftwareElementDTO aim2 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element2", "family2", null, null));
        PedagogicalSoftwareElementDTO aim3 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element3", "family3", null, null));
        PedagogicalSoftwareElementDTO aim5 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element5", "family5", null, null));

        List<PedagogicalSoftwareElementDTO> originElements;
        List<PedagogicalSoftwareElementDTO> aimElements;
        Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities;
        Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences;

        NextStepHint nextSteps = null;

        //A- Same origin and aim comparison
        originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(0, nextSteps.getAddElements().size());
        assertEquals(0, nextSteps.getDeleteElements().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        //B- More in origin
        originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(0, nextSteps.getAddElements().size());
        assertEquals(1, nextSteps.getDeleteElements().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element3", nextSteps.getDeleteElements().get(0).getElementName());


        //C- More in aim comparison
        originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2));
        aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddElements().size());
        assertEquals(0, nextSteps.getDeleteElements().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element3", nextSteps.getAddElements().get(0).getElementName());

        //D- Difference in origin and in aim
        originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddElements().size());
        assertEquals(1, nextSteps.getDeleteElements().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element5", nextSteps.getAddElements().get(0).getElementName());
        assertEquals("element4", nextSteps.getDeleteElements().get(0).getElementName());


        //E- Difference in origin and in aim, but more (one element repeated) in origin
        originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2, origin4, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddElements().size());
        assertEquals(2, nextSteps.getDeleteElements().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element5", nextSteps.getAddElements().get(0).getElementName());
        assertEquals("element4", nextSteps.getDeleteElements().get(0).getElementName());
        assertEquals("element4", nextSteps.getDeleteElements().get(0).getElementName());

    }
}

package artie.pedagogicalintervention.webservice.test;

import artie.common.web.dto.NextStepHint;
import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import artie.pedagogicalintervention.webservice.service.DistanceCalculationService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PedagogicalSoftwareServiceNextStepTest {

    private PedagogicalSoftwareService pedagogicalSoftwareService;
    private DistanceCalculationService distanceCalculationService;

    @BeforeEach
    void setUp() throws Exception {
        pedagogicalSoftwareService = new PedagogicalSoftwareService();
        distanceCalculationService = new DistanceCalculationService();
    }

    @Test
    void familyNextStepsCalculationTest() {

        //Setup
        PedagogicalSoftwareBlockDTO origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1","family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family2", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family3", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin4 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element4", "family4", null, null, null, null, null));

        PedagogicalSoftwareBlockDTO aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1","family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family2", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family3", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim5 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element5", "family5", null, null, null, null, null));

        List<PedagogicalSoftwareBlockDTO> originElements;
        List<PedagogicalSoftwareBlockDTO> aimElements;
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities;
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences;

        NextStepHint nextSteps = null;

        //A- Same origin and aim comparison
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        //B- More in origin
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element3", nextSteps.getDeleteBlocks().get(0).getBlockName());


        //C- More in aim comparison
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element3", nextSteps.getAddBlocks().get(0).getBlockName());

        //D- Difference in origin and in aim
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());
        assertEquals("element4", nextSteps.getDeleteBlocks().get(0).getBlockName());


        //E- Difference in origin and in aim, but more (one element repeated) in origin
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(2, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());

        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());
        assertEquals("element4", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element4", nextSteps.getDeleteBlocks().get(0).getBlockName());

    }

    @Test
    void elementNextStepsCalculationTest(){
        //Setup
        PedagogicalSoftwareBlockDTO origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),0);
        PedagogicalSoftwareBlockDTO origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null),1);
        PedagogicalSoftwareBlockDTO origin3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null),2);
        PedagogicalSoftwareBlockDTO origin4 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element4", "family1", null, null, null, null, null),3);

        PedagogicalSoftwareBlockDTO aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),0);
        PedagogicalSoftwareBlockDTO aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null),1);
        PedagogicalSoftwareBlockDTO aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null),2);
        PedagogicalSoftwareBlockDTO aim5 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element5", "family1", null, null, null, null, null),3);

        List<PedagogicalSoftwareBlockDTO> aimElements;
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities = new HashMap<>();
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences = new HashMap<>();
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapElementSimilarities;

        NextStepHint nextSteps = null;


        //A- Same origin and aim comparison
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());


        //B- More in aim than in origin
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals("element2", nextSteps.getAddBlocks().get(0).getBlockName());


        //C- More in aim with 2 family difference distance
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(origin3)));
        mapFamilyDifferences.put("family4", new ArrayList<>(Arrays.asList(origin4)));
        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals("element3", nextSteps.getAddBlocks().get(0).getBlockName());


        //D- Difference in origin and aim with 2 family difference distance
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(origin3, aim3)));
        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());
        assertEquals("element4", nextSteps.getDeleteBlocks().get(0).getBlockName());


        //E- Repeated element in origin but not in aim
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin1, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals("element1", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());


        //F- Repeated element in origin, but not in aim, and with different positions
        PedagogicalSoftwareBlockDTO origin1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),2);
        PedagogicalSoftwareBlockDTO origin1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),3);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1bis, origin1bis2, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals("element1", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());


        //G- Repeated element 3 times in origin, but not in aim, and with different positions
        origin1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),2);
        origin1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),3);
        PedagogicalSoftwareBlockDTO aim1bis3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),2);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1bis, origin1bis2, aim1bis3, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(2, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals("element1", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element1", nextSteps.getDeleteBlocks().get(1).getBlockName());
        assertEquals("element5", nextSteps.getAddBlocks().get(0).getBlockName());


        //H- Repeated element in aim, but not in origin, and with different positions
        PedagogicalSoftwareBlockDTO aim1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),0);
        PedagogicalSoftwareBlockDTO aim1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),1);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null),2);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1bis, aim1bis2, aim2));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getAddBlocks().size());
        assertEquals("element3", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element1", nextSteps.getAddBlocks().get(0).getBlockName());


        //I- Repeated element 3 times in aim, but not in origin, and with different positions
        aim1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),0);
        aim1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),1);
        aim1bis3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),2);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null),3);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1bis, aim1bis2, aim1bis3, aim2));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, nextSteps);

        assertEquals(1, nextSteps.getDeleteBlocks().size());
        assertEquals(2, nextSteps.getAddBlocks().size());
        assertEquals("element3", nextSteps.getDeleteBlocks().get(0).getBlockName());
        assertEquals("element1", nextSteps.getAddBlocks().get(0).getBlockName());
        assertEquals("element1", nextSteps.getAddBlocks().get(1).getBlockName());

    }

    @Test
    void inputNextStepsCalculationTest() {

        //Setup
        PedagogicalSoftwareBlockDTO origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO diff1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family2", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO diff2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family3", null, null, null, null, null));

        PedagogicalSoftwareInput originInput1;
        PedagogicalSoftwareField originFieldInput1;

        PedagogicalSoftwareInput originInput2;
        PedagogicalSoftwareField originFieldInput2;


        PedagogicalSoftwareInput aimInput1;
        PedagogicalSoftwareField aimFieldInput1;

        PedagogicalSoftwareInput aimInput2;
        PedagogicalSoftwareField aimFieldInput2;

        PedagogicalSoftwareInput diffInput1;
        PedagogicalSoftwareField diffFieldInput1;

        PedagogicalSoftwareInput diffInput2;
        PedagogicalSoftwareField diffFieldInput2;


        Map<String, List<PedagogicalSoftwareBlockDTO>> mapElementSimilarities = new HashMap<>();
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences = new HashMap<>();
        List<PedagogicalSoftwareBlockDTO> aimElements;
        NextStepHint nextSteps = null;


        //A- Same origin and aim
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        originFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
        aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));


        mapElementSimilarities.put("element1", new ArrayList<>(Arrays.asList(origin1)));
        aimElements = new ArrayList<>(Arrays.asList(aim1));

        distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(0, nextSteps.getReplaceInputs().size());


        //B- String input difference
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        originFieldInput1 = new PedagogicalSoftwareField("STR", "b");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
        aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));

        diffFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        diffFieldInput2 = new PedagogicalSoftwareField("NUM", "20");
        diffInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput1)));
        diffInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput2)));
        diff1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput1)));
        diff2.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput2)));


        mapElementSimilarities.clear();
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        mapFamilyDifferences.put("family2", new ArrayList<>(Arrays.asList(diff1)));
        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(diff2)));

        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1));

        distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getReplaceInputs().size());

        assertEquals("element1", nextSteps.getReplaceInputs().get(0).getElement().getBlockName());
        assertEquals("b", nextSteps.getReplaceInputs().get(0).getInputValue());
        assertEquals("a", nextSteps.getReplaceInputs().get(0).getSolutionValue());


        //C- Number input difference
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        originFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "40");
        aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
        aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));

        mapElementSimilarities.clear();
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1));

        diffFieldInput1 = new PedagogicalSoftwareField("STR", "string");
        diffFieldInput2 = new PedagogicalSoftwareField("NUM", "15");
        diffInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput1)));
        diffInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput2)));
        diff1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput1)));
        diff2.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput2)));

        mapFamilyDifferences.put("family2", new ArrayList<>(Arrays.asList(diff1)));
        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(diff2)));

        distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(1, nextSteps.getReplaceInputs().size());

        assertEquals("element1", nextSteps.getReplaceInputs().get(0).getElement().getBlockName());
        assertEquals("30.0", nextSteps.getReplaceInputs().get(0).getInputValue());
        assertEquals("40.0", nextSteps.getReplaceInputs().get(0).getSolutionValue());


        //D- Number and String inputs difference
        mapFamilyDifferences = new HashMap<>();
        nextSteps = new NextStepHint();

        originFieldInput1 = new PedagogicalSoftwareField("STR", "b");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "40");
        aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
        aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));

        mapElementSimilarities.clear();
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1));

        diffFieldInput1 = new PedagogicalSoftwareField("STR", "string");
        diffFieldInput2 = new PedagogicalSoftwareField("NUM", "90");
        diffInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput1)));
        diffInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(diffFieldInput2)));
        diff1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput1)));
        diff2.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(diffInput2)));

        mapFamilyDifferences.put("family2", new ArrayList<>(Arrays.asList(diff1)));
        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(diff2)));

        distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, nextSteps);

        assertEquals(0, nextSteps.getAddBlocks().size());
        assertEquals(0, nextSteps.getDeleteBlocks().size());
        assertEquals(2, nextSteps.getReplaceInputs().size());

        assertEquals("element1", nextSteps.getReplaceInputs().get(0).getElement().getBlockName());
        assertEquals("b", nextSteps.getReplaceInputs().get(0).getInputValue());
        assertEquals("a", nextSteps.getReplaceInputs().get(0).getSolutionValue());

        assertEquals("element1", nextSteps.getReplaceInputs().get(1).getElement().getBlockName());
        assertEquals("30.0", nextSteps.getReplaceInputs().get(1).getInputValue());
        assertEquals("40.0", nextSteps.getReplaceInputs().get(1).getSolutionValue());

    }
}

package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.service.DistanceCalculationService;

public class DistanceCalculationServiceTest {

    private DistanceCalculationService distanceCalculationService;


    @BeforeEach
    void setUp() throws Exception {
        distanceCalculationService = new DistanceCalculationService();
    }
    @Test
    void familyDistanceCalculationTest() {

        //Setup
        PedagogicalSoftwareBlockDTO origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("", "element1","family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family2", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family3", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO origin4 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family4", null, null, null, null, null));

        PedagogicalSoftwareBlockDTO aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1","family1", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family2", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family3", null, null, null, null, null));
        PedagogicalSoftwareBlockDTO aim5 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family5", null, null, null, null, null));

        List<PedagogicalSoftwareBlockDTO> originElements;
        List<PedagogicalSoftwareBlockDTO> aimElements;
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities;
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences;

        //A- Same origin and aim comparison
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        double distance = distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);

        assertEquals(0, distance);
        assertEquals(3, mapFamilySimilarities.size());
        assertTrue(mapFamilySimilarities.containsKey("family1"));
        assertTrue(mapFamilySimilarities.containsKey("family2"));
        assertTrue(mapFamilySimilarities.containsKey("family3"));
        assertEquals(1, mapFamilySimilarities.get("family1").size());
        assertEquals(1, mapFamilySimilarities.get("family2").size());
        assertEquals(1, mapFamilySimilarities.get("family3").size());
        assertEquals(0, mapFamilyDifferences.size());


        //B- More in origin
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);

        assertEquals(1, distance);
        assertEquals(2, mapFamilySimilarities.size());
        assertTrue(mapFamilySimilarities.containsKey("family1"));
        assertTrue(mapFamilySimilarities.containsKey("family2"));
        assertEquals(1, mapFamilySimilarities.get("family1").size());
        assertEquals(1, mapFamilySimilarities.get("family2").size());
        assertEquals(1, mapFamilyDifferences.size());
        assertTrue(mapFamilyDifferences.containsKey("family3"));
        assertEquals(1, mapFamilyDifferences.get("family3").size());


        //C- More in aim comparison
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);

        assertEquals(1, distance);
        assertEquals(2, mapFamilySimilarities.size());
        assertTrue(mapFamilySimilarities.containsKey("family1"));
        assertTrue(mapFamilySimilarities.containsKey("family2"));
        assertEquals(1, mapFamilySimilarities.get("family1").size());
        assertEquals(1, mapFamilySimilarities.get("family2").size());
        assertEquals(1, mapFamilyDifferences.size());
        assertTrue(mapFamilyDifferences.containsKey("family3"));
        assertEquals(1, mapFamilyDifferences.get("family3").size());


        //D- Difference in origin and in aim
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);

        assertEquals(2, distance);
        assertEquals(2, mapFamilySimilarities.size());
        assertTrue(mapFamilySimilarities.containsKey("family1"));
        assertTrue(mapFamilySimilarities.containsKey("family2"));
        assertEquals(1, mapFamilySimilarities.get("family1").size());
        assertEquals(1, mapFamilySimilarities.get("family2").size());
        assertEquals(2, mapFamilyDifferences.size());
        assertTrue(mapFamilyDifferences.containsKey("family4"));
        assertTrue(mapFamilyDifferences.containsKey("family5"));
        assertEquals(1, mapFamilyDifferences.get("family4").size());
        assertEquals(1, mapFamilyDifferences.get("family5").size());


        //E- Difference in origin and in aim, but more (one element repeated) in origin
        originElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4, origin4));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapFamilySimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieFamilyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);

        assertEquals(2, distance);
        assertEquals(2, mapFamilySimilarities.size());
        assertTrue(mapFamilySimilarities.containsKey("family1"));
        assertTrue(mapFamilySimilarities.containsKey("family2"));
        assertEquals(1, mapFamilySimilarities.get("family1").size());
        assertEquals(1, mapFamilySimilarities.get("family2").size());
        assertEquals(2, mapFamilyDifferences.size());
        assertTrue(mapFamilyDifferences.containsKey("family4"));
        assertTrue(mapFamilyDifferences.containsKey("family5"));
        assertEquals(2, mapFamilyDifferences.get("family4").size());
        assertEquals(1, mapFamilyDifferences.get("family5").size());

    }


    @Test
    void elementDistanceCalculationTest() {

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


        //A- Same origin and aim comparison
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        double distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(0, distance);
        assertEquals(3, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertTrue(mapElementSimilarities.containsKey("element3"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());
        assertEquals(1, mapElementSimilarities.get("element3").size());


        //B- More in origin with 1 family difference distance
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        mapFamilyDifferences.put("family4", new ArrayList<>(Arrays.asList(origin4)));
        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(2, distance); //Element distance = 1 + Family difference distance = 1
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());


        //C- More in aim with 2 family difference distance
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(origin3)));
        mapFamilyDifferences.put("family4", new ArrayList<>(Arrays.asList(origin4)));
        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(3, distance); //Element distance = 1 + Family difference distance = 2
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());


        //D- Difference in origin and aim with 2 family difference distance
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin4)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(origin3, aim3)));
        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(4, distance); //Element distance = 2 + Family difference distance = 2
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());


        //E- Repeated element in origin but not in aim
        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin1, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(2, distance);
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());
        assertEquals(0, mapElementSimilarities.get("element1").get(0).getElementPosition());
        assertEquals(1, mapElementSimilarities.get("element2").get(0).getElementPosition());


        //F- Repeated element in origin, but not in aim, and with different positions
        PedagogicalSoftwareBlockDTO origin1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),2);
        PedagogicalSoftwareBlockDTO origin1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),3);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1bis, origin1bis2, origin2)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim5));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(2, distance);
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());
        assertEquals(2, mapElementSimilarities.get("element1").get(0).getElementPosition());
        assertEquals(1, mapElementSimilarities.get("element2").get(0).getElementPosition());


        //G- Repeated element in aim, but not in origin, and with different positions
        PedagogicalSoftwareBlockDTO aim1bis = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),0);
        PedagogicalSoftwareBlockDTO aim1bis2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null),1);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null),2);

        mapFamilySimilarities.clear();
        mapFamilySimilarities.put("family1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1, origin2, origin3)));
        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1bis, aim1bis2, aim2));
        mapElementSimilarities = new HashMap<>();
        mapFamilyDifferences = new HashMap<>();

        distance = distanceCalculationService.artieElementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

        assertEquals(2, distance);
        assertEquals(2, mapElementSimilarities.size());
        assertTrue(mapElementSimilarities.containsKey("element1"));
        assertTrue(mapElementSimilarities.containsKey("element2"));
        assertEquals(1, mapElementSimilarities.get("element1").size());
        assertEquals(1, mapElementSimilarities.get("element2").size());
        assertEquals(0, mapElementSimilarities.get("element1").get(0).getElementPosition());
        assertEquals(1, mapElementSimilarities.get("element2").get(0).getElementPosition());

    }

    @Test
    void inputDistanceCalculationTest() {

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


        //A.1- Same origin and aim and different CASE
        mapFamilyDifferences = new HashMap<>();

        originFieldInput1 = new PedagogicalSoftwareField("str", "a");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "A");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        aimInput1 = new PedagogicalSoftwareInput("NAME", "NAME", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
        aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));


        mapElementSimilarities.put("element1", new ArrayList<>(Arrays.asList(origin1)));
        aimElements = new ArrayList<>(Arrays.asList(aim1));

        double distance = distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(0, distance);


        //B- String input difference and different CASE
        mapFamilyDifferences = new HashMap<>();

        originFieldInput1 = new PedagogicalSoftwareField("str", "b");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "A");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        aimInput1 = new PedagogicalSoftwareInput("NAME", "NAME", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
        aimInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
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

        distance = distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(22, distance); //Input difference from similar families: 1 + Input differences from different families: 20 + 1 (string)


        //C- Number input difference and different CASE
        mapFamilyDifferences = new HashMap<>();

        originFieldInput1 = new PedagogicalSoftwareField("str", "a");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "A");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "40");
        aimInput1 = new PedagogicalSoftwareInput("NAME", "NAME", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
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

        distance = distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(16.25, distance); //Input difference from similar families: 0.25 + Input differences from different families: 15 + 1 (string)


        //D- Number input difference with 0 value in the aim and different CASE
        mapFamilyDifferences = new HashMap<>();

        originFieldInput1 = new PedagogicalSoftwareField("str", "a");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "A");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "0");
        aimInput1 = new PedagogicalSoftwareInput("NAME", "NAME", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
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

        distance = distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(46, distance); //Input difference from similar families: 0.25 + Input differences from different families: 15 + 1 (string)


        //E- Number and String inputs difference and different CASE
        mapFamilyDifferences = new HashMap<>();

        originFieldInput1 = new PedagogicalSoftwareField("str", "b");
        originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
        originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
        originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
        origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


        aimFieldInput1 = new PedagogicalSoftwareField("STR", "A");
        aimFieldInput2 = new PedagogicalSoftwareField("NUM", "40");
        aimInput1 = new PedagogicalSoftwareInput("NAME", "NAME", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
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

        distance = distanceCalculationService.artieInputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(92.25, distance); //Input difference from similar families: 1.25 + Input differences from different families: 90 + 1 (string)

    }


    @Test
    void positionDistanceCalculationTest() {

        //Setup
        PedagogicalSoftwareBlockDTO origin1;
        PedagogicalSoftwareBlockDTO origin2;
        PedagogicalSoftwareBlockDTO origin3;

        PedagogicalSoftwareBlockDTO aim1;
        PedagogicalSoftwareBlockDTO aim2;
        PedagogicalSoftwareBlockDTO aim3;

        PedagogicalSoftwareBlockDTO diff1;
        PedagogicalSoftwareBlockDTO diff2;

        Map<String, List<PedagogicalSoftwareBlockDTO>> mapElementSimilarities = new HashMap<>();
        Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences = new HashMap<>();
        List<PedagogicalSoftwareBlockDTO> aimElements;

        //A- Same positions in origin and aim
        origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 1);
        origin3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null), 2);

        aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 1);
        aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null), 2);

        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        mapElementSimilarities.put("element2", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin2)));
        mapElementSimilarities.put("element3", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin3)));

        double distance = distanceCalculationService.artiePositionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(0, distance);


        //B- Different positions in origin and aim with same elements
        mapElementSimilarities.clear();
        mapFamilyDifferences = new HashMap<>();

        origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 2);
        origin3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null), 3);

        aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 1);
        aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element3", "family1", null, null, null, null, null), 2);

        diff1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family2", null, null, null, null, null), 2);
        diff2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family3", null, null, null, null, null), 3);

        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim2, aim3));
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        mapElementSimilarities.put("element2", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin2)));
        mapElementSimilarities.put("element3", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin3)));

        mapFamilyDifferences.put("family2", new ArrayList<>(Arrays.asList(diff1)));
        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(diff2)));

        distance = distanceCalculationService.artiePositionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(9, distance); //Position difference from similar families: 2 + Position differences from different families: (2+1) + (3+1)


        //C- More elements in aim than in origin
        mapElementSimilarities.clear();
        mapFamilyDifferences = new HashMap<>();

        origin1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        origin2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 1);

        aim1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 0);
        aim2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family1", null, null, null, null, null), 1);
        aim3 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family1", null, null, null, null, null), 2);

        diff1 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element1", "family2", null, null, null, null, null), 0);
        diff2 = new PedagogicalSoftwareBlockDTO(new PedagogicalSoftwareBlock("","element2", "family3", null, null, null, null, null), 1);

        aimElements = new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(aim1, aim1, aim3));
        mapElementSimilarities.put("element1", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin1)));
        mapElementSimilarities.put("element2", new ArrayList<PedagogicalSoftwareBlockDTO>(Arrays.asList(origin2)));

        mapFamilyDifferences.put("family2", new ArrayList<>(Arrays.asList(diff1)));
        mapFamilyDifferences.put("family3", new ArrayList<>(Arrays.asList(diff2)));

        distance = distanceCalculationService.artiePositionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

        assertEquals(5, distance); //Position difference from similar families: 2 + Position differences from different families: (0+1) + (1+1)
    }
}

package artie.pedagogicalintervention.webservice.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;

class PedagogicalSoftwareServiceTest {

	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	
	@BeforeEach
	void setUp() throws Exception {
		pedagogicalSoftwareService = new PedagogicalSoftwareService();		
	}

	@Test
	void getAllElementsTest() {
		
		PedagogicalSoftwareBlock e1 = new PedagogicalSoftwareBlock("","element1","family1", null, null, null, null, null);
		PedagogicalSoftwareBlock e11 = new PedagogicalSoftwareBlock("","element11", "family11", null, null, null, null, null);
		PedagogicalSoftwareBlock e12 = new PedagogicalSoftwareBlock("","element12", "family12", null, null, null, null, null);
		PedagogicalSoftwareBlock e121 = new PedagogicalSoftwareBlock("","element121", "family121", null, null, null, null, null);
		PedagogicalSoftwareBlock e122 = new PedagogicalSoftwareBlock("","element122", "family122", null, null, null, null, null);
		PedagogicalSoftwareBlock e13 = new PedagogicalSoftwareBlock("","element13", "family13", null, null, null, null, null);
		PedagogicalSoftwareBlock e131 = new PedagogicalSoftwareBlock("","element131", "family131", null, null, null, null, null);
		PedagogicalSoftwareBlock e132 = new PedagogicalSoftwareBlock("","element132", "family132", null, null, null, null, null);
		
		PedagogicalSoftwareBlock e2 = new PedagogicalSoftwareBlock("","element2", "family2", null, null, null, null, null);
		PedagogicalSoftwareBlock e21 = new PedagogicalSoftwareBlock("","element21", "family21", null, null, null, null, null);
		PedagogicalSoftwareBlock e211 = new PedagogicalSoftwareBlock("","element211", "family211", null, null, null, null, null);
		PedagogicalSoftwareBlock e212 = new PedagogicalSoftwareBlock("","element212", "family212", null, null, null, null, null);
		PedagogicalSoftwareBlock e22 = new PedagogicalSoftwareBlock("","element22", "family22", null, null, null, null, null);
		
		List<PedagogicalSoftwareBlockDTO> elements = new ArrayList<>();
		
		AtomicInteger position = new AtomicInteger(0);
		
		
		//A- Testing simple next elements
		position.set(0);
		elements.clear();
		
		e1.setNext(e2);
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(2, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(1, elements.get(1).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element2", elements.get(1).getElementName());
		
		
		//B- Testing simple next elements nested elements
		position.set(0);
		elements.clear();
		e1.setNext(null);
		
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(2, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(2, elements.get(1).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		
		
		//C- Testing a simple next and nested elements
		position.set(0);
		elements.clear();
		
		e1.setNext(e2);
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(3, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(2, elements.get(1).getElementPosition());
		assertEquals(3, elements.get(2).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		assertEquals("element2", elements.get(2).getElementName());
		
		
		//D- Testing a simple next and medium nested elements
		position.set(0);
		elements.clear();
		
		e11.setNext(e12);
		e1.setNext(e2);
		
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(4, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(3, elements.get(1).getElementPosition());
		assertEquals(4, elements.get(2).getElementPosition());
		assertEquals(5, elements.get(3).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		assertEquals("element12", elements.get(2).getElementName());
		assertEquals("element2", elements.get(3).getElementName());
		
		
		//E- Testing a simple next and complex nested elements
		position.set(0);
		elements.clear();
		
		e121.setNext(e122);
		e12.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e121)));
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		e1.setNext(e2);
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(6, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(5, elements.get(1).getElementPosition());
		assertEquals(6, elements.get(2).getElementPosition());
		assertEquals(9, elements.get(3).getElementPosition());
		assertEquals(10, elements.get(4).getElementPosition());
		assertEquals(11, elements.get(5).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		assertEquals("element12", elements.get(2).getElementName());
		assertEquals("element121", elements.get(3).getElementName());
		assertEquals("element122", elements.get(4).getElementName());
		assertEquals("element2", elements.get(5).getElementName());
		
		
		//F- Testing a medium next and complex nested elements
		position.set(0);
		elements.clear();
		
		e121.setNext(e122);
		e12.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e121)));
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		
		e21.setNext(e22);
		e2.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e21)));
		e1.setNext(e2);
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(8, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(5, elements.get(1).getElementPosition());
		assertEquals(6, elements.get(2).getElementPosition());
		assertEquals(9, elements.get(3).getElementPosition());
		assertEquals(10, elements.get(4).getElementPosition());
		assertEquals(11, elements.get(5).getElementPosition());
		assertEquals(14, elements.get(6).getElementPosition());
		assertEquals(15, elements.get(7).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		assertEquals("element12", elements.get(2).getElementName());
		assertEquals("element121", elements.get(3).getElementName());
		assertEquals("element122", elements.get(4).getElementName());
		assertEquals("element2", elements.get(5).getElementName());
		assertEquals("element21", elements.get(6).getElementName());
		assertEquals("element22", elements.get(7).getElementName());
		
		
		//G- Testing a complex next and very complex nested elements
		position.set(0);
		elements.clear();
		
		e131.setNext(e132);
		e13.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e131)));
		
		e121.setNext(e122);
		e12.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e121)));
		e12.setNext(e13);
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e11)));
		
		e211.setNext(e212);
		e21.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e211)));
		e21.setNext(e22);
		e2.setNested(new ArrayList<PedagogicalSoftwareBlock>(Arrays.asList(e21)));
		e1.setNext(e2);
		
		elements = this.pedagogicalSoftwareService.getAllElements(e1, elements, position);
		
		assertEquals(13, elements.size());
		assertEquals(0, elements.get(0).getElementPosition());
		assertEquals(8, elements.get(1).getElementPosition());
		assertEquals(9, elements.get(2).getElementPosition());
		assertEquals(12, elements.get(3).getElementPosition());
		assertEquals(13, elements.get(4).getElementPosition());
		assertEquals(14, elements.get(5).getElementPosition());
		assertEquals(17, elements.get(6).getElementPosition());
		assertEquals(18, elements.get(7).getElementPosition());
		assertEquals(19, elements.get(8).getElementPosition());
		assertEquals(24, elements.get(9).getElementPosition());
		assertEquals(27, elements.get(10).getElementPosition());
		assertEquals(28, elements.get(11).getElementPosition());
		assertEquals(29, elements.get(12).getElementPosition());
		assertEquals("element1", elements.get(0).getElementName());
		assertEquals("element11", elements.get(1).getElementName());
		assertEquals("element12", elements.get(2).getElementName());
		assertEquals("element121", elements.get(3).getElementName());
		assertEquals("element122", elements.get(4).getElementName());
		assertEquals("element13", elements.get(5).getElementName());
		assertEquals("element131", elements.get(6).getElementName());
		assertEquals("element132", elements.get(7).getElementName());
		assertEquals("element2", elements.get(8).getElementName());
		assertEquals("element21", elements.get(9).getElementName());
		assertEquals("element211", elements.get(10).getElementName());
		assertEquals("element212", elements.get(11).getElementName());
		assertEquals("element22", elements.get(12).getElementName());
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
		
		double distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);
		
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
		
		distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);
		
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
		
		distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);
		
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
		
		distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);
		
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
		
		distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, 0, null);
		
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

		double distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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
		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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
		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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
		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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

		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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

		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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

		distance = pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, 0, null);

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
		

		//A- Same origin and aim
		mapFamilyDifferences = new HashMap<>();
		
		originFieldInput1 = new PedagogicalSoftwareField("STR", "a");
		originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
		originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
		originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
		origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));
		
		
		aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
		aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
		aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
		aimInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput2)));
		aim1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(aimInput1, aimInput2)));
		
		
		mapElementSimilarities.put("element1", new ArrayList<>(Arrays.asList(origin1)));
		aimElements = new ArrayList<>(Arrays.asList(aim1));
		
		double distance = pedagogicalSoftwareService.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
		assertEquals(0, distance);
		
		
		//B- String input difference
		mapFamilyDifferences = new HashMap<>();
		
		originFieldInput1 = new PedagogicalSoftwareField("STR", "b");
		originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
		originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
		originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
		origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));
		
		
		aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
		aimFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
		aimInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(aimFieldInput1)));
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
		
		distance = pedagogicalSoftwareService.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
		assertEquals(22, distance); //Input difference from similar families: 1 + Input differences from different families: 20 + 1 (string)
		
		
		//C- Number input difference
		mapFamilyDifferences = new HashMap<>();
		
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
		
		distance = pedagogicalSoftwareService.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
		assertEquals(16.25, distance); //Input difference from similar families: 0.25 + Input differences from different families: 15 + 1 (string)


		//D- Number input difference with 0 value in the aim
		mapFamilyDifferences = new HashMap<>();

		originFieldInput1 = new PedagogicalSoftwareField("STR", "a");
		originFieldInput2 = new PedagogicalSoftwareField("NUM", "30");
		originInput1 = new PedagogicalSoftwareInput("Name", "Name", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput1)));
		originInput2 = new PedagogicalSoftwareInput("Steps", "Steps", new ArrayList<PedagogicalSoftwareField>(Arrays.asList(originFieldInput2)));
		origin1.setInputs(new ArrayList<PedagogicalSoftwareInput>(Arrays.asList(originInput1, originInput2)));


		aimFieldInput1 = new PedagogicalSoftwareField("STR", "a");
		aimFieldInput2 = new PedagogicalSoftwareField("NUM", "0");
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

		distance = pedagogicalSoftwareService.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);

		assertEquals(46, distance); //Input difference from similar families: 0.25 + Input differences from different families: 15 + 1 (string)
		
		
		//E- Number and String inputs difference
		mapFamilyDifferences = new HashMap<>();
		
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
		
		distance = pedagogicalSoftwareService.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
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
	
		double distance = pedagogicalSoftwareService.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
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
	
		distance = pedagogicalSoftwareService.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
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
	
		distance = pedagogicalSoftwareService.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, 0, null);
		
		assertEquals(5, distance); //Position difference from similar families: 2 + Position differences from different families: (0+1) + (1+1)


	}
}



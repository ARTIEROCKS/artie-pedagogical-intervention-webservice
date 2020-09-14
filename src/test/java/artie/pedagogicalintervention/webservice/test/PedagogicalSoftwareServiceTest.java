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

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;

class PedagogicalSoftwareServiceTest {

	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	
	@BeforeEach
	void setUp() throws Exception {
		pedagogicalSoftwareService = new PedagogicalSoftwareService();		
	}

	@Test
	void getAllElementsTest() {
		
		PedagogicalSoftwareElement e1 = new PedagogicalSoftwareElement("element1","family1", null, null);
		PedagogicalSoftwareElement e11 = new PedagogicalSoftwareElement("element11", "family11", null, null);
		PedagogicalSoftwareElement e12 = new PedagogicalSoftwareElement("element12", "family12", null, null);
		PedagogicalSoftwareElement e121 = new PedagogicalSoftwareElement("element121", "family121", null, null);
		PedagogicalSoftwareElement e122 = new PedagogicalSoftwareElement("element122", "family122", null, null);
		PedagogicalSoftwareElement e13 = new PedagogicalSoftwareElement("element13", "family13", null, null);
		PedagogicalSoftwareElement e131 = new PedagogicalSoftwareElement("element131", "family131", null, null);
		PedagogicalSoftwareElement e132 = new PedagogicalSoftwareElement("element132", "family132", null, null);
		
		PedagogicalSoftwareElement e2 = new PedagogicalSoftwareElement("element2", "family2", null, null);
		PedagogicalSoftwareElement e21 = new PedagogicalSoftwareElement("element21", "family21", null, null);
		PedagogicalSoftwareElement e211 = new PedagogicalSoftwareElement("element211", "family211", null, null);
		PedagogicalSoftwareElement e212 = new PedagogicalSoftwareElement("element212", "family212", null, null);
		PedagogicalSoftwareElement e22 = new PedagogicalSoftwareElement("element22", "family22", null, null);
		
		List<PedagogicalSoftwareElementDTO> elements = new ArrayList<>();
		
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
		
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
		
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
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
		
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
		
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
		
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
		e12.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e121)));
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
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
		e12.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e121)));
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
		
		e21.setNext(e22);
		e2.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e21)));
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
		e13.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e131)));
		
		e121.setNext(e122);
		e12.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e121)));
		e12.setNext(e13);
		e11.setNext(e12);
		e1.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e11)));
		
		e211.setNext(e212);
		e21.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e211)));
		e21.setNext(e22);
		e2.setNested(new ArrayList<PedagogicalSoftwareElement>(Arrays.asList(e21)));
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
		
		//A- Simple comparison
		PedagogicalSoftwareElementDTO origin1 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1","family1", null, null));
		PedagogicalSoftwareElementDTO origin2 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1", "family2", null, null));
		PedagogicalSoftwareElementDTO origin3 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1", "family3", null, null));
		
		PedagogicalSoftwareElementDTO aim1 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1","family1", null, null));
		PedagogicalSoftwareElementDTO aim2 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1", "family2", null, null));
		PedagogicalSoftwareElementDTO aim3 = new PedagogicalSoftwareElementDTO(new PedagogicalSoftwareElement("element1", "family3", null, null));
		
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(origin1, origin2, origin3));
		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<PedagogicalSoftwareElementDTO>(Arrays.asList(aim1, aim2, aim3));
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		
		double distance = pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, 0);
		
		assertEquals(0, distance);
		assertEquals(3, mapFamilySimilarities.size());
		assertTrue(mapFamilySimilarities.containsKey("family1"));
		assertTrue(mapFamilySimilarities.containsKey("family2"));
		assertTrue(mapFamilySimilarities.containsKey("family3"));
		assertEquals(1, mapFamilySimilarities.get("family1").size());
		assertEquals(1, mapFamilySimilarities.get("family2").size());
		assertEquals(1, mapFamilySimilarities.get("family3").size());
	}
}



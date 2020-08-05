package artie.pedagogicalintervention.webservice.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;

class PedagogicalSoftwareServiceTest {

	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	//Origin elements
	private PedagogicalSoftwareElement elementOrigin1;
	private PedagogicalSoftwareElement elementOrigin2;
	private PedagogicalSoftwareElement elementOrigin3;
	
	//Aim elements
	private PedagogicalSoftwareElement elementAim1;
	private PedagogicalSoftwareElement elementAim2;
	private PedagogicalSoftwareElement elementAim3;
	
	@BeforeEach
	void setUp() throws Exception {
		
		//Sets the pedagogical software service
		this.pedagogicalSoftwareService= new PedagogicalSoftwareService();
		
		//Sets the different fields
		List<PedagogicalSoftwareField> fields1 = new ArrayList<>();
		PedagogicalSoftwareField field11 = new PedagogicalSoftwareField("fieldName11", "fieldValue11");
		PedagogicalSoftwareField field12 = new PedagogicalSoftwareField("fieldName12", "fieldValue12");
		fields1.add(field11);
		fields1.add(field12);
		
		List<PedagogicalSoftwareField> fields2 = new ArrayList<>();
		PedagogicalSoftwareField field21 = new PedagogicalSoftwareField("fieldName21", "fieldValue21");
		PedagogicalSoftwareField field22 = new PedagogicalSoftwareField("fieldName22", "fieldValue22");
		fields2.add(field21);
		fields2.add(field22);
		
		//Sets the different inputs
		List<PedagogicalSoftwareInput> inputs1 = new ArrayList<>();
		PedagogicalSoftwareInput input11 = new PedagogicalSoftwareInput("input11", fields1);
		PedagogicalSoftwareInput input21 = new PedagogicalSoftwareInput("input11", fields2);
		inputs1.add(input11);
		inputs1.add(input21);
		
		//Sets the origin elements
		this.elementOrigin3 = new PedagogicalSoftwareElement("element3", "family3", inputs1, null);
		this.elementOrigin2 = new PedagogicalSoftwareElement("element2", "family1", inputs1, elementOrigin3);
		this.elementOrigin1 = new PedagogicalSoftwareElement("element1", "family1", inputs1, elementOrigin2);
		
		//Sets the aim elements
		this.elementAim3 = new PedagogicalSoftwareElement("element4", "family4", inputs1, null);
		this.elementAim2 = new PedagogicalSoftwareElement("element3", "family1", inputs1, elementAim3);
		this.elementAim1 = new PedagogicalSoftwareElement("element1", "family1", inputs1, elementAim2);
	}

	@Test
	void getAllElementsTest() {
		
		//Sets the needed variables
		List<PedagogicalSoftwareElementDTO> elements = new ArrayList<>();
		int order = 0;
		
		//Calls the function
		elements = this.pedagogicalSoftwareService.getAllElements(elementOrigin1, elements, order);
		
		//Checks that the number of elements corresponds with the number of elements
		assertTrue(elements.size() == 3);
		
		//Checks the orders
		assertTrue(elements.get(0).getElementName() == "element1");
		assertTrue(elements.get(0).getElementOrder() == 0);
		
		assertTrue(elements.get(1).getElementName() == "element2");
		assertTrue(elements.get(1).getElementOrder() == 1);
		
		assertTrue(elements.get(2).getElementName() == "element3");
		assertTrue(elements.get(2).getElementOrder() == 2);
		
	}
	
	@Test
	void familyDistanceCalculationTest() {
		
		//Sets the needed variables
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		
		//Gets the elements not nested
		int order = 0;
		originElements = this.pedagogicalSoftwareService.getAllElements(elementOrigin1, originElements, order);
		
		order = 0;
		aimElements = this.pedagogicalSoftwareService.getAllElements(elementAim1, aimElements, order);
		
		//Calls the family distance calculation
		long diffFamily = 0;
		diffFamily = this.pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, diffFamily);
		
		//Checks the calculated family difference
		assertEquals(diffFamily, 1);
		
		//Checks the family map
		assertEquals(mapFamilySimilarities.keySet().size(), 1);
		assertEquals(mapFamilySimilarities.keySet().toArray()[0], "family1");
		assertEquals(mapFamilySimilarities.get("family1").size(), 2);		
	}
	
	@Test
	void elementDistanceCalculationTest() {
		
		//Sets the needed variables
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities = new HashMap<>();
		
		//Gets the elements not nested
		int order = 0;
		originElements = this.pedagogicalSoftwareService.getAllElements(elementOrigin1, originElements, order);
		
		order = 0;
		aimElements = this.pedagogicalSoftwareService.getAllElements(elementAim1, aimElements, order);
		
		//Calls the family distance calculation
		long diffFamily = 0;
		diffFamily = this.pedagogicalSoftwareService.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, diffFamily);
		
		//Calls the element distance calculation
		long diffElement = 0;
		diffElement = this.pedagogicalSoftwareService.elementDistanceCalculation(mapFamilySimilarities, mapElementSimilarities, aimElements, diffElement);
		
		//Checks the calculated element difference
		assertEquals(diffElement, 1);
		
		//Checks the elements map
		assertEquals(mapElementSimilarities.keySet().size(), 1);
		assertEquals(mapElementSimilarities.keySet().toArray()[0], "element1");
		assertEquals(mapElementSimilarities.get("element1").size(), 1);
	}

}

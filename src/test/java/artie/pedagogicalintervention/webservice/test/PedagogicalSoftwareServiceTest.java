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
	void calculateGradeTest(){

		double result = 0;
		result = pedagogicalSoftwareService.calculateGrade(380, 380, 10);
		assertEquals(0, result);

		result = pedagogicalSoftwareService.calculateGrade(380, 190, 10);
		assertEquals(5, result);

		result = pedagogicalSoftwareService.calculateGrade(380, 0, 10);
		assertEquals(10, result);
	}
}



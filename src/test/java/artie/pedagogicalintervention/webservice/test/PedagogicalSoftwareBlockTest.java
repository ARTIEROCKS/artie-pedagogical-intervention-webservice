package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedagogicalSoftwareBlockTest {
	
	private PedagogicalSoftwareBlock element1;
	private PedagogicalSoftwareBlock element11;
	private PedagogicalSoftwareBlock element2;
	private PedagogicalSoftwareBlock element21;
	private PedagogicalSoftwareBlock element22;
	private PedagogicalSoftwareBlock element23;

	@BeforeEach
	void setUp() throws Exception {
		
		List<PedagogicalSoftwareField> fields1 = new ArrayList<>();
		List<PedagogicalSoftwareField> fields2 = new ArrayList<>();
		List<PedagogicalSoftwareField> fields21 = new ArrayList<>();
		
		List<PedagogicalSoftwareInput> inputs1 = new ArrayList<>();
		List<PedagogicalSoftwareInput> inputs2 = new ArrayList<>();
		List<PedagogicalSoftwareInput> inputs21 = new ArrayList<>();
		
		List<PedagogicalSoftwareBlock> nested1 = new ArrayList<>();
		List<PedagogicalSoftwareBlock> nested2 = new ArrayList<>();
		List<PedagogicalSoftwareBlock> nested21 = new ArrayList<>();
		
		
		
		//Data for elements 1 and 11
		fields1.add(new PedagogicalSoftwareField("field1", "value1"));
		fields1.add(new PedagogicalSoftwareField("field11", "value11"));
		
		inputs1.add(new PedagogicalSoftwareInput("input1", "input1", fields1));
		
		nested1.add(new PedagogicalSoftwareBlock("","nested1", "nestedFamily1", inputs1, null, null, null, null));
		
		
		this.element1 = new PedagogicalSoftwareBlock("","name1", "family1", inputs1, null, null, null, null);
		this.element11 = new PedagogicalSoftwareBlock("","name1", "family1", inputs1, null, null, null, null);
		
		this.element1.setNested(nested1);
		this.element11.setNested(nested1);
		
		
		//Data for elements 2, 21 and 22
		fields2.add(new PedagogicalSoftwareField("field2", "value2"));
		fields2.add(new PedagogicalSoftwareField("field21", "value21"));
		
		fields21.add(new PedagogicalSoftwareField("field21", "value21"));
		fields21.add(new PedagogicalSoftwareField("field211", "value211"));
		
		inputs2.add(new PedagogicalSoftwareInput("input2", "input2", fields2));
		inputs21.add(new PedagogicalSoftwareInput("input2", "input2", fields21));
		
		nested2.add(new PedagogicalSoftwareBlock("","nested2", "nestedFamily2", inputs2, null, null, null, null));
		nested21.add(new PedagogicalSoftwareBlock("","nested21", "nestedFamily21", inputs21, null, null, null, null));
		
		this.element2 = new PedagogicalSoftwareBlock("","name2", "family2", inputs2, null, null, null, null);
		this.element21 = new PedagogicalSoftwareBlock("","name2", "family2", inputs21, null, null, null, null);
		this.element22 = new PedagogicalSoftwareBlock("","name2", "family22", inputs21, null, null, null, null);
		this.element23 = new PedagogicalSoftwareBlock("","name23", "family22", inputs21, null, null, null, null);
		
		this.element2.setNested(nested2);
		this.element21.setNested(nested21);
		this.element22.setNested(nested21);
		this.element23.setNested(nested21);
	}

	@Test
	void equalsElementTest() {
		assertEquals(this.element1, this.element11);
		assertNotEquals(this.element1, this.element2);
		assertNotEquals(this.element2, this.element21);
		assertNotEquals(this.element21, this.element22);
		assertNotEquals(this.element22, this.element23);
	}

}

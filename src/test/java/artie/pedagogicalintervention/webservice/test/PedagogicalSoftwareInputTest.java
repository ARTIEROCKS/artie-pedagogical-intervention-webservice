package artie.pedagogicalintervention.webservice.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;

class PedagogicalSoftwareInputTest {

	private PedagogicalSoftwareInput input1;
	private PedagogicalSoftwareInput input11;
	private PedagogicalSoftwareInput input111;
	private PedagogicalSoftwareInput input2;
	private PedagogicalSoftwareInput input21;
	private PedagogicalSoftwareInput input22;
	
	@BeforeEach
	void setUp() throws Exception {
		List<PedagogicalSoftwareField> fields1 = new ArrayList<>();
		List<PedagogicalSoftwareField> fields2 = new ArrayList<>();
		
		fields1.add(new PedagogicalSoftwareField("name1", "value1"));
		fields1.add(new PedagogicalSoftwareField("name11", "value11"));
		
		fields2.add(new PedagogicalSoftwareField("name2", "value2"));
		fields2.add(new PedagogicalSoftwareField("name21", "value21"));
		
		
		this.input1 = new PedagogicalSoftwareInput("name1", "name1", fields1);
		this.input11 = new PedagogicalSoftwareInput("Name1", "Name1", fields1);
		this.input111 = new PedagogicalSoftwareInput("NAME1", "NAME1", fields1);
		
		this.input2 = new PedagogicalSoftwareInput("name2", "name2", fields2);
		this.input21 = new PedagogicalSoftwareInput("name21", "name21", fields2);
		this.input22 = new PedagogicalSoftwareInput("name21", "name21", fields1);
		
	}

	@Test
	void equalsInputTest() {
		assertEquals(input1, input1);
		assertEquals(input1, input11);
		assertEquals(input1, input111);
		assertNotEquals(input1, input2);
		assertNotEquals(input2, input21);
		assertNotEquals(input21, input22);
	}

}

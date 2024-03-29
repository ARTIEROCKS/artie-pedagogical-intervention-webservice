package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

	@Test
	void toStringInputTest(){
		assertEquals(input1.toString(),"(name1-name1[$name1:value1$$name11:value11$])");
		assertEquals(input11.toString(),"(Name1-Name1[$name1:value1$$name11:value11$])");
		assertEquals(input111.toString(),"(NAME1-NAME1[$name1:value1$$name11:value11$])");
		assertEquals(input2.toString(),"(name2-name2[$name2:value2$$name21:value21$])");
		assertEquals(input21.toString(),"(name21-name21[$name2:value2$$name21:value21$])");
		assertEquals(input22.toString(),"(name21-name21[$name1:value1$$name11:value11$])");
	}

}

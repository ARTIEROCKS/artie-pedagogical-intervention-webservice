package artie.pedagogicalintervention.webservice.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;

class PedagogicalSoftwareFieldTest {

	private PedagogicalSoftwareField field1;
	private PedagogicalSoftwareField field11;
	private PedagogicalSoftwareField field21;
	private PedagogicalSoftwareField field22;
	
	
	@BeforeEach
	void setUp() throws Exception {
		this.field1 = new PedagogicalSoftwareField("field1", "value1");
		this.field11 = new PedagogicalSoftwareField("field1", "value1");
		this.field21 = new PedagogicalSoftwareField("field2", "value1");
		this.field22 = new PedagogicalSoftwareField("field2", "value2");
	}
	
	@Test
	void equalsFieldTest() {
		
		assertEquals(this.field1, this.field11);
		assertNotEquals(this.field1, this.field21);
		assertNotEquals(this.field1, this.field22);
		assertNotEquals(this.field21, this.field22);
	}

}

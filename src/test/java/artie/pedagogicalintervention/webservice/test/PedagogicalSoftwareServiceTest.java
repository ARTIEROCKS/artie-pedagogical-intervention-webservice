package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedagogicalSoftwareServiceTest {

	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	
	@BeforeEach
	void setUp() {
		pedagogicalSoftwareService = new PedagogicalSoftwareService();		
	}


	@Test
	void calculateGradeTest(){

		double result = pedagogicalSoftwareService.calculateGrade(380, 380, 10);
		assertEquals(0, result);

		result = pedagogicalSoftwareService.calculateGrade(380, 190, 10);
		assertEquals(5, result);

		result = pedagogicalSoftwareService.calculateGrade(380, 0, 10);
		assertEquals(10, result);
	}
}



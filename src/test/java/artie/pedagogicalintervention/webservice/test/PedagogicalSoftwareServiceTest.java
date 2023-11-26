package artie.pedagogicalintervention.webservice.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;

class PedagogicalSoftwareServiceTest {

	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	
	@BeforeEach
	void setUp() throws Exception {
		pedagogicalSoftwareService = new PedagogicalSoftwareService();		
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



package artie.pedagogicalintervention.webservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import artie.common.web.dto.Student;
import artie.sensor.common.dto.SensorObject;

@Service
public class SensorService {
	
	public void getEmotionalState(List<SensorObject> data, Student student, String user, String password) {
		//TODO: Send the information to the emotional webservice to get the emotional state
	}
}

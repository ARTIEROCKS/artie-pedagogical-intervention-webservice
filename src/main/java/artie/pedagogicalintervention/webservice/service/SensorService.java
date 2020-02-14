package artie.pedagogicalintervention.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import artie.common.web.dto.Student;
import artie.pedagogicalintervention.webservice.model.SensorData;
import artie.pedagogicalintervention.webservice.repository.SensorDataRepository;
import artie.sensor.common.dto.SensorObject;

@Service
public class SensorService {
	
	@Autowired
	private SensorDataRepository sensorDataRepository;
	
	/**
	 * Adds the sensor data in the database
	 * @param data
	 * @param student
	 */
	public void add(List<SensorObject> data, Student student) {
		
		//Gets all the sensor objects and inserts it in the database
		data.forEach(d -> {
			
			SensorData sd;
			try {
				sd = new SensorData(d, student);
				this.sensorDataRepository.save(sd);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
		});
		
	}
	
	/**
	 * Get the emotional state
	 * @param data
	 * @param student
	 * @param user
	 * @param password
	 */
	public void getEmotionalState(List<SensorObject> data, Student student, String user, String password) {
		//TODO: Send the information to the emotional webservice to get the emotional state
	}
}

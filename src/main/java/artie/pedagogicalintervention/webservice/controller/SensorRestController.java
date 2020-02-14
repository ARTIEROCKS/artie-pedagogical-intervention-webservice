package artie.pedagogicalintervention.webservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import artie.common.web.dto.SecuritySensorData;
import artie.pedagogicalintervention.webservice.service.SecurityService;
import artie.pedagogicalintervention.webservice.service.SensorService;

@Controller
@RestController
@RequestMapping("/api/v1/sensor")
public class SensorRestController {
	
	@Autowired
	private SensorService sensorService;
	
	@Autowired
	private SecurityService securityService;
	
	
	/**
	 * Function to receive the sensor data
	 * @param data
	 * @param student
	 * @param user
	 * @param password
	 */
	@PostMapping(path = "/sendSensorData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void sendSensorData(@RequestBody SecuritySensorData securitySensorData) {
		
		//1- Login into the system
		if(securityService.login(securitySensorData.getUser(), securitySensorData.getPassword())) {
			
			//1.1- Adds the sensor data to the database
			this.sensorService.add(securitySensorData.getData(), securitySensorData.getStudent());
			
			//1.2- Getting the emotional state
			this.sensorService.getEmotionalState(securitySensorData.getData(), securitySensorData.getStudent(), securitySensorData.getUser(), securitySensorData.getPassword());
			
		}
		
	}
}

package artie.pedagogicalintervention.webservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import artie.common.web.dto.Student;
import artie.pedagogicalintervention.webservice.service.SecurityService;
import artie.pedagogicalintervention.webservice.service.SensorService;
import artie.sensor.common.dto.SensorObject;

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
	@GetMapping(path = "/sendSensorData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void sendSensorData(@RequestParam("data") List<SensorObject> data, 
							   @RequestParam("student") Student student, 
							   @RequestParam("userName") String user, 
							   @RequestParam("password") String password) {
		
		//1- Login into the system
		if(securityService.login(user, password)) {
			
			//2- Getting the emotional state
			this.sensorService.getEmotionalState(data, student, user, password);
			
		}
		
	}
}

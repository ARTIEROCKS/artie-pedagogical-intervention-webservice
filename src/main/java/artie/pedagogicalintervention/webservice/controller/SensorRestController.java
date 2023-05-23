package artie.pedagogicalintervention.webservice.controller;


import artie.pedagogicalintervention.webservice.service.EmotionalStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import artie.common.web.dto.SecuritySensorData;
import artie.pedagogicalintervention.webservice.service.SecurityService;

@Controller
@RestController
@RequestMapping("/api/v1/sensor")
@CrossOrigin(origins="*", allowedHeaders="*")
public class SensorRestController {

	@Autowired
	private EmotionalStateService emotionalStateService;
	
	@Autowired
	private SecurityService securityService;
	
	
	/**
	 * Function to receive the sensor data
	 * @param securitySensorData
	 */
	@PostMapping(path = "/sendSensorData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public void sendSensorData(@RequestBody SecuritySensorData securitySensorData) {
		
		//1- Login into the system
		if(securityService.login(securitySensorData.getUser(), securitySensorData.getPassword())) {
			//1.1 Adds the sensor data to the queue
			this.emotionalStateService.sendEmotionalStateMessage(securitySensorData.getData(), securitySensorData.getStudent().getId());
		}
		
	}
}

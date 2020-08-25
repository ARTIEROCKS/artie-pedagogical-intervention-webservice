package artie.pedagogicalintervention.webservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareSolutionService;


@Controller
@RestController
@RequestMapping("/api/v1/pedagogicalsoftware")
@CrossOrigin(origins="*", allowedHeaders="*")
public class PedagogicalSoftwareRestController {

	@Autowired
	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	@Autowired
	private PedagogicalSoftwareSolutionService pedagogicalSoftwareSolutionService;
	
	/**
	 * Function to store the pedagogical software data
	 * @param data, the elements in the workspace
	 */
	@PostMapping(path = "/sendPedagogicalSoftwareData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void sendPedagogicalSoftwareData(@RequestBody String data) {
		this.pedagogicalSoftwareService.add(data);
	}
	
	/**
	 * Function to add the pedagogical software solution
	 * @param data, the solution to the exercise
	 */
	@PostMapping(path = "/addPedagogicalSoftwareSolution",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void addPedagogicalSoftwareSolution(@RequestBody String data) {
		this.pedagogicalSoftwareSolutionService.add(data);
	}
	
}

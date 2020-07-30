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

import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;


@Controller
@RestController
@RequestMapping("/api/v1/pedagogicalsoftware")
public class PedagogicalSoftwareRestController {

	@Autowired
	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	/**
	 * Function to store the pedagogical software data
	 * @param data
	 */
	@PostMapping(path = "/sendPedagogicalSoftwareData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void sendPedagogicalSoftwareData(@RequestBody String data) {
		this.pedagogicalSoftwareService.add(data);
	}
	
}

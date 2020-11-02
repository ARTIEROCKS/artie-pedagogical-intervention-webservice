package artie.pedagogicalintervention.webservice.controller;

import artie.common.web.dto.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareSolutionService;

import java.util.List;


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
	@ResponseStatus(HttpStatus.CREATED)
	public String sendPedagogicalSoftwareData(@RequestBody String data) {
		return this.pedagogicalSoftwareService.add(data);
	}
	
	/**
	 * Function to store the pedagogical software solution
	 * @param data, the solution to the exercise
	 */
	@PostMapping(path = "/sendPedagogicalSoftwareSolution",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public String sendPedagogicalSoftwareSolution(@RequestBody String data) {
		return this.pedagogicalSoftwareSolutionService.add(data);
	}

	/**
	 * Function to get the finished exercises by the students of an user
	 * @param userId
	 * @return
	 */
	@GetMapping(path = "/finishedExercises",
				produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public Exercise[] getFinishedExercisesByUserId(@RequestParam String userId){
		List<Exercise> listExercises = this.pedagogicalSoftwareService.findFinishedExercisesByUserId(userId);
		return listExercises.toArray(new Exercise[listExercises.size()]);
	}

	/**
	 * Function to set the validated value in a finished exercise
	 * @param pedagogicalDataId
	 * @param validated
	 */
	@GetMapping(path = "/finishedExercises/validate",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public void validateFinishedExerciseByPedagogicalDataId(@RequestParam String pedagogicalDataId, @RequestParam int validated){
		this.pedagogicalSoftwareService.validateFinishedExerciseByPedagogicalDataId(pedagogicalDataId, validated);
	}
	
}

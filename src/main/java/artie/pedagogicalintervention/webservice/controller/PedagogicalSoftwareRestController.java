package artie.pedagogicalintervention.webservice.controller;

import artie.common.web.dto.Exercise;
import artie.common.web.dto.LearningProgress;
import artie.common.web.dto.Solution;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.service.InterventionService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareSolutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RestController
@RequestMapping("/api/v1/pedagogicalsoftware")
@CrossOrigin(origins="*", allowedHeaders="*")
public class PedagogicalSoftwareRestController {

	@Autowired
	private PedagogicalSoftwareService pedagogicalSoftwareService;
	
	@Autowired
	private PedagogicalSoftwareSolutionService pedagogicalSoftwareSolutionService;

	@Autowired
	private InterventionService interventionService;
	
	/**
	 * Function to store the pedagogical software data
	 * @param data, the elements in the workspace
	 */
	@PostMapping(path = "/sendPedagogicalSoftwareData",
	         produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public String sendPedagogicalSoftwareData(@RequestBody String data) throws JsonProcessingException {

		//We build the intervention to the robot queue
		this.interventionService.buildAndSendIntervention(data);
		return this.pedagogicalSoftwareService.add(data);
	}

	/**
	 * Function to update if the user answered yes to the question if he/she needs help
	 * @param id
	 * @param answeredNeedHelp
	 * @return
	 */
	@PutMapping(path = "/update/answeredNeedHelp",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String updateAnsweredNeedHelp(@RequestParam String id, @RequestParam boolean answeredNeedHelp) throws JsonProcessingException {

		//If the student answers he/she needs help
		if (answeredNeedHelp) {
			this.interventionService.buildAndSendInterventionByPedagogicalSoftwareDataId(id);
		}
		return this.pedagogicalSoftwareService.updateAnsweredNeedHelpById(id, answeredNeedHelp);
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
	 * Function to get the finished exercises by the students of a user
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
	 * Function to get the finished exercises by student id
	 * @param studentId
	 * @return
	 */
	@GetMapping(path = "/finishedExercisesByStudentId",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public Exercise[] getFinishedExercisesByStudentId(@RequestParam String studentId){
		List<Exercise> listExercises = this.pedagogicalSoftwareService.findFinishedExercisesByStudentId(studentId);
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

	/**
	 * Function to get the solutions of a user ID
	 * @param userId
	 * @return
	 */
	@GetMapping(path = "/solutions",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public Solution[] getSolutionsByUserId(@RequestParam String userId){
		List<PedagogicalSoftwareSolution> tmpListSolutions = this.pedagogicalSoftwareSolutionService.findByUserId(userId);
		List<Solution> listSolutions = tmpListSolutions.stream().map(s -> new Solution(s.getId(), s.getExerciseId(), s.getExercise().getName(), s.getExercise().getDescription(),
																					   s.getScreenShot(), s.getBinary())).collect(Collectors.toList());
		return listSolutions.toArray(new Solution[listSolutions.size()]);
	}

	/**
	 * Function to delete a solution
	 * @param solutionId
	 * @return
	 */
	@GetMapping(path = "/solutions/delete",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteSolutionById(@RequestParam String solutionId){
		this.pedagogicalSoftwareSolutionService.deleteSolutionById(solutionId);
	}

	/**
	 * Function to get the learning progress of a student in a specific exercise
	 * @return learning progress
	 */
	@GetMapping(path = "/learningProgress/getByExerciseAndStudent",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.FOUND)
	public LearningProgress[] getLearningProgressByExerciseAndStudent(@RequestParam String studentId, @RequestParam String exerciseId){

		List<LearningProgress> learningProgressList = this.pedagogicalSoftwareService.findByStudentAndExercise(studentId, exerciseId);
		return learningProgressList.toArray(new LearningProgress[learningProgressList.size()]);
	}
	
}

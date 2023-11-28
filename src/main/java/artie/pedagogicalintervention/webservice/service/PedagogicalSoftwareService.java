package artie.pedagogicalintervention.webservice.service;

import artie.common.web.dto.*;
import artie.common.web.enums.ResponseCodeEnum;
import artie.common.web.enums.ValidSolutionEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareSolutionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedagogicalSoftwareService {

	@Value("${artie.api.key}")
	private String apiKey;
	private RestTemplate restTemplate;
	private HttpEntity<String> entity;

	@Autowired
	private PedagogicalSoftwareDataRepository pedagogicalSoftwareDataRepository;

	@Autowired
	private PedagogicalSoftwareSolutionRepository pedagogicalSoftwareSolutionRepository;

	@Autowired
	private HelpModelService helpModelService;

	@Autowired
	private DistanceCalculationService distanceCalculationService;

	@Value("${artie.webservices.student.updateCompetence.url}")
	private String updateCompetenceUrl;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public PedagogicalSoftwareService(RestTemplateBuilder builder){
		this.restTemplate = builder.build();
	}
	public PedagogicalSoftwareService(){}

	@PostConstruct
	public void setUp(){
		this.objectMapper.registerModule(new JavaTimeModule());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("apiKey", this.apiKey);
		this.entity = new HttpEntity<String>("parameters", headers);
	}

	/**
	 * Function to add the pedagogical software data in the database
	 * 
	 * @param pedagogicalSoftwareData
	 */
	public String add(PedagogicalSoftwareData pedagogicalSoftwareData) {

		Response response = new Response(null);

		// 1- Looks for the solution to the exercise
		List<PedagogicalSoftwareSolution> pedagogicalSoftwareSolution = this.pedagogicalSoftwareSolutionRepository.findByExercise_IdAndUserId(pedagogicalSoftwareData.getExercise().getId(), pedagogicalSoftwareData.getStudent().getUserId());

		// 2- If there is at least 1 solution, we get the distances
		SolutionDistance distance = null;
		double maximumDistance = 0;
		double grade = 0;
		if (pedagogicalSoftwareSolution != null && pedagogicalSoftwareSolution.size() > 0) {

			//2.1- Gets the distance
			Map<String, Object> mapDistance = distanceCalculationService.distanceCalculation(pedagogicalSoftwareData, pedagogicalSoftwareSolution);
			distance = (SolutionDistance)mapDistance.get("distance");
			maximumDistance = (double)mapDistance.get("maximumDistance");
			pedagogicalSoftwareData.setSolutionDistance(distance);

			//2.2- Calculates and sets the grade
			grade = this.calculateGrade(maximumDistance, distance.getTotalDistance(), 10);
			pedagogicalSoftwareData.setGrade(grade);

			//2.3- We look if the exercise is an evaluation or not, and distance is 0, and the student has not set the competence
			if(pedagogicalSoftwareData.getExercise().isEvaluation() && distance.getTotalDistance() == 0 & pedagogicalSoftwareData.getStudent().getCompetence() == 0){
				//We set the competence as the level of the exercise
				pedagogicalSoftwareData.getStudent().setCompetence(pedagogicalSoftwareData.getExercise().getLevel());
				ResponseEntity<Response> wsResponse = this.restTemplate.exchange(this.updateCompetenceUrl + "?studentId=" + pedagogicalSoftwareData.getStudent().getId() + "&competence=" + pedagogicalSoftwareData.getExercise().getLevel(), HttpMethod.GET, this.entity, Response.class);
				response = wsResponse.getBody();
			}else if (pedagogicalSoftwareData.getExercise().isEvaluation() && distance.getTotalDistance() == 0){
				//If the exercise is an evaluation, and the distance is 0, we set the competence of the student
				int level = (pedagogicalSoftwareData.getExercise().getLevel() - 1 == 0 ? pedagogicalSoftwareData.getExercise().getLevel() : pedagogicalSoftwareData.getExercise().getLevel() - 1);
				pedagogicalSoftwareData.getStudent().setCompetence(level);
				ResponseEntity<Response> wsResponse = this.restTemplate.exchange(this.updateCompetenceUrl + "?studentId=" + pedagogicalSoftwareData.getStudent().getId() + "&competence=" + level, HttpMethod.GET, this.entity, Response.class);
				response = wsResponse.getBody();
			}
		}

		PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);

		//3- Creating the return object
		HelpResult helpResult = new HelpResult(objSaved.getId(), objSaved.isPredictedNeedHelp(), false, null, distance);
		if(pedagogicalSoftwareData.isRequestHelp() && distance == null){
			//3.1- If the distance is null, and we have requested help, there must be an error
			response = new Response(new ResponseBody(ResponseCodeEnum.ERROR.toString()));
		}else{
			//3.2- We send that everything is OK and the help result object
			response = new Response(new ResponseBody(ResponseCodeEnum.OK.toString(), helpResult));
		}

		return response.toJSON();
	}

	/**
	 * Function to transform a pedagogical software data from string to object
	 * 
	 * @param psd
	 */
	public String add(String psd) {

		String response = "";

		try {

			// 1. Transforms the string into the pedagogical software data
			PedagogicalSoftwareData pedagogicalSoftwareData = this.objectMapper.readValue(psd,
					PedagogicalSoftwareData.class);

			// 2.1 Calls the help model to get if the help must be shown or not
			boolean helpNeeded = false;
			try{
				helpNeeded = this.helpModelService.predict(pedagogicalSoftwareData);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			// 2.2 Adds the predicted need help into the response
			pedagogicalSoftwareData.setPredictedNeedHelp(helpNeeded);
			response = this.add(pedagogicalSoftwareData);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * Function to get the finished exercises of a user ID and which total distance is > 0
	 * @param userId
	 * @return
	 */
	public List<Exercise> findFinishedExercisesByUserId(String userId){

		//1- Gets the finished exercises of the user ID
		List<Exercise> listFinishedExercises = this.pedagogicalSoftwareDataRepository.findByFinishedExercise(true)
																		.stream()
																		.filter(fe -> (fe.getStudent().getUserId().equals(userId) &&
																							(fe.getSolutionDistance().getTotalDistance() > 0 ||
																									fe.getSolutionDistance().getTotalDistance() == -1 ||
																									fe.getValidSolution() == ValidSolutionEnum.REJECTED.getValue() ||
																									fe.getValidSolution() == ValidSolutionEnum.VALIDATED.getValue()))
																		)
																		.map(e ->{
																			return new Exercise(e.getId(), e.getExercise().getName(), e.getExercise().getDescription(), e.getExercise().getFinishedExerciseId(),
																							    e.getScreenShot(), e.getBinary(), e.getValidSolution(), e.getExercise().isEvaluation(), e.getExercise().getLevel());
																		})
																		.collect(Collectors.toList());

		return listFinishedExercises;
	}

	/**
	 * Function to get the finished exercises of a student ID or which total distance is == 0
	 * @param studentId
	 * @return
	 */
	public List<Exercise> findFinishedExercisesByStudentId(String studentId){

		List<Exercise> listFinishedExercises = this.pedagogicalSoftwareDataRepository.findByStudent_Id(studentId)
												.stream()
												.filter(fe -> fe.getSolutionDistance().getTotalDistance() == 0 ||
														(fe.isFinishedExercise() && fe.getValidSolution() == ValidSolutionEnum.VALIDATED.getValue()))
												.map(e ->{
													return new Exercise(e.getExercise().getId(), e.getExercise().getName(), e.getExercise().getDescription(), e.getExercise().getFinishedExerciseId(),
															e.getScreenShot(), e.getBinary(), e.getValidSolution(), e.getExercise().isEvaluation(), e.getExercise().getLevel());
												})
												.collect(Collectors.toList());

		return StreamEx.of(listFinishedExercises).distinct(Exercise::getId).toList();

	}

	/**
	 * Function to set if a finished exercise has been validated or not
	 * @param pedagogicalDataId
	 * @param validated
	 */
	public void validateFinishedExerciseByPedagogicalDataId(String pedagogicalDataId, int validated){

		//1- Searches the pedagogical software data by its ID
		PedagogicalSoftwareData pedagogicalSoftwareData = this.pedagogicalSoftwareDataRepository.findById(pedagogicalDataId).orElse(null);

		//2- Sets the validated value
		if(pedagogicalSoftwareData != null){

			//2.1- If there is a validation
			if(validated == ValidSolutionEnum.VALIDATED.getValue()){

				//We set the distance of the pedagogical software data to 0
				pedagogicalSoftwareData.setSolutionDistance(new SolutionDistance("",0,0,0,0,0, null));

				//We register the new solution
				PedagogicalSoftwareSolution pedagogicalSoftwareSolution = new PedagogicalSoftwareSolution(pedagogicalSoftwareData.getStudent().getUserId(),
						pedagogicalSoftwareData.getId(),
						pedagogicalSoftwareData.getExercise(),
						pedagogicalSoftwareData.getScreenShot(),
						pedagogicalSoftwareData.getBinary(),
						pedagogicalSoftwareData.getElements(), 0);

				//Calculates the maximum distance for this solution
				SolutionDistance pedagogicalSoftwareDistance = distanceCalculationService.distanceCalculation(new PedagogicalSoftwareData(), pedagogicalSoftwareSolution);

				//Sets the maximum distance to this solution
				pedagogicalSoftwareSolution.setMaximumDistance(pedagogicalSoftwareDistance.getTotalDistance());

				//Save the pedagogical software solution in the database
				this.pedagogicalSoftwareSolutionRepository.save(pedagogicalSoftwareSolution);

			}else{

				//we delete the solution
				List<PedagogicalSoftwareSolution> pedagogicalSoftwareSolutions = this.pedagogicalSoftwareSolutionRepository.findByPedagogicalSoftwareDataId(pedagogicalDataId);
				this.pedagogicalSoftwareSolutionRepository.deleteAll(pedagogicalSoftwareSolutions);

				//We calculate the distance and the grade of the pedagogical software data
				List<PedagogicalSoftwareSolution> listSolutions = this.pedagogicalSoftwareSolutionRepository.findByExercise_IdAndUserId(pedagogicalSoftwareData.getExercise().getId(), pedagogicalSoftwareData.getStudent().getUserId());
				Map<String, Object> mapDistance = distanceCalculationService.distanceCalculation(pedagogicalSoftwareData, listSolutions);
				SolutionDistance pedagogicalSoftwareDistance = (SolutionDistance) mapDistance.get("distance");
				pedagogicalSoftwareData.setSolutionDistance(pedagogicalSoftwareDistance);

				double maximumDistance = (double)mapDistance.get("maximumDistance");
				double grade = this.calculateGrade(maximumDistance, pedagogicalSoftwareDistance.getTotalDistance(), 10);
				pedagogicalSoftwareData.setGrade(grade);
			}

			pedagogicalSoftwareData.setValidSolution(validated);
			this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);
		}
	}

	/**
	 * Function that calculates a grade in base of the current distance with a maximum distance
	 * @param maximumDistance
	 * @param currentDistance
	 * @param maximumGrade
	 * @return
	 */
	public double calculateGrade(double maximumDistance, double currentDistance, double maximumGrade){

		//1- Checks that the current distance is not bigger than the maximum
		currentDistance = currentDistance > maximumDistance ? maximumDistance : currentDistance;

		//2- Formula that calculates the grade
		return (maximumGrade - ((maximumGrade*currentDistance) / maximumDistance));
	}

	/**
	 * Function to return all the pedagogical software data interactions of a student and an exercise
	 * @param studentId student ID
	 * @param exerciseId exercise ID
	 * @return
	 */
	public List<LearningProgress> findByStudentAndExercise(String studentId, String exerciseId){

		//Gets all the pedagogical software data of the student and the selected exercise
		List<PedagogicalSoftwareData> pedagogicalSoftwareDataList = this.pedagogicalSoftwareDataRepository.findByStudent_IdAndExerciseId(studentId, exerciseId);

		//Transforms this information in a learning progress list
        return pedagogicalSoftwareDataList.stream().map(ps -> {
			return new LearningProgress(ps.getExercise(), ps.getStudent(), ps.getSolutionDistance().getTotalDistance(), ps.getGrade(),
										ps.getDateTime(), ps.getLastLogin(), ps.isRequestHelp(), ps.getSecondsHelpOpen(), ps.isFinishedExercise(),
										ps.getValidSolution());
		}).collect(Collectors.toList());
	}

	/**
	 * Function to find the pedagogical software data from its id
	 * @param id
	 * @return
	 */
	public PedagogicalSoftwareData findById(String id){
		return this.pedagogicalSoftwareDataRepository.findById(id).orElse(null);
	}

	/**
	 * Function to update the answeredNeededHelp in base of the id of the PedagogicalSoftwareData
	 * @param id
	 */
	public String updateAnsweredNeedHelpById(String id, boolean answeredNeedHelp){

		Response response = new Response();

		//Updates the pedagogical software data with the answered need help information
		PedagogicalSoftwareData psd = this.pedagogicalSoftwareDataRepository.findById(id).orElse(null);

		// If we have the pedagogical software data
		if(psd != null){

			psd.setAnsweredNeedHelp(answeredNeedHelp);

			// We check if the user wants help
			if(answeredNeedHelp && psd.getSolutionDistance() != null && psd.getSolutionDistance().getSolutionId() != null){

				// 1- We get the solution of the pedagogical software data
				PedagogicalSoftwareSolution pss = this.pedagogicalSoftwareSolutionRepository.findById(psd.getSolutionDistance().getSolutionId()).orElse(null);

				// 2- Calculates the distance and the next steps
				SolutionDistance solutionDistance = distanceCalculationService.distanceCalculation(psd, pss);

				// 3- Gets the new grade of the user
				double newGrade = this.calculateGrade(pss.getMaximumDistance(), solutionDistance.getTotalDistance(), 10);

				// 4- Updates the pedagogical software data
				psd.setSolutionDistance(solutionDistance);
				psd.setGrade(newGrade);
			}

			this.pedagogicalSoftwareDataRepository.save(psd);
			response.setBody(new ResponseBody(ResponseCodeEnum.OK.toString(), psd));
		} else{
			response.setBody(new ResponseBody(ResponseCodeEnum.ERROR.toString()));
		}
		return response.toJSON();
	}
}

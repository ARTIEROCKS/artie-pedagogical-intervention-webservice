package artie.pedagogicalintervention.webservice.service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import artie.common.web.dto.*;
import artie.common.web.enums.ResponseCodeEnum;
import artie.common.web.enums.ValidSolutionEnum;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareSolutionRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.enums.DistanceEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

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
			Map<String, Object> mapDistance = this.distanceCalculation(pedagogicalSoftwareData, pedagogicalSoftwareSolution);
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
				SolutionDistance pedagogicalSoftwareDistance = this.distanceCalculation(new PedagogicalSoftwareData(), pedagogicalSoftwareSolution);

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
				Map<String, Object> mapDistance = this.distanceCalculation(pedagogicalSoftwareData, listSolutions);
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
	 * Nearest distance calculation between an element and a list of solutions
	 * @param origin
	 * @param aims
	 * @return
	 */
	public Map<String, Object> distanceCalculation(PedagogicalSoftwareData origin, List<PedagogicalSoftwareSolution> aims){

		Map<String, Object> result = new HashMap<>();
		SolutionDistance nearestDistance = null;
		double maximumDistance = 0;

		//1- Gets the distance between all the solutions
		for(PedagogicalSoftwareSolution aim : aims){
			SolutionDistance distance = this.distanceCalculation(origin, aim);

			//2- Sets the nearest distance
			if(nearestDistance == null || distance.getTotalDistance() < nearestDistance.getTotalDistance()){
				nearestDistance = distance;
				maximumDistance = aim.getMaximumDistance();
			}
		}

		result.put("distance", nearestDistance);
		result.put("maximumDistance", maximumDistance);
		return result;
	}

	/**
	 * Distance calculation between an element and its aim
	 * 
	 * @param origin
	 * @param aim
	 * @return
	 */
	public SolutionDistance distanceCalculation(PedagogicalSoftwareData origin, PedagogicalSoftwareSolution aim) {

		List<PedagogicalSoftwareBlockDTO> aimBlocks = new ArrayList<>();
		List<PedagogicalSoftwareBlockDTO> originBlocks = new ArrayList<>();

		//Preparing the next steps in base if the user has requested help or not
		NextStepHint nextSteps = ((origin.isRequestHelp() || origin.isAnsweredNeedHelp()) ? new NextStepHint() : null);

		// Family variables
		Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities = new HashMap<>();
		Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences = new HashMap<>();
		double diffFamily = 0;

		// Element variables
		Map<String, List<PedagogicalSoftwareBlockDTO>> mapElementSimilarities = new HashMap<>();
		double diffElements = 0;

		// Position variables
		double diffPosition = 0;

		// Input values variables
		double diffInput = 0;

		// total distance
		double totalDistance = 0;

		// 1- Getting all the blocks in a single list (not nested)
		for (PedagogicalSoftwareBlock element : aim.getAllBlocks()) {
			aimBlocks = this.getAllElements(element, aimBlocks, new AtomicInteger(0));
		}
		for (PedagogicalSoftwareBlock block : origin.getAllBlocks()) {
			originBlocks = this.getAllElements(block, originBlocks, new AtomicInteger(0));

		}

		// 2- Family differences and similarities
		diffFamily = this.familyDistanceCalculation(aimBlocks, originBlocks, mapFamilySimilarities, mapFamilyDifferences, diffFamily, nextSteps);

		// 3- Element similarities from the family similarities
		diffElements = this.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimBlocks, diffElements, nextSteps);

		// We can now delete the family similarities map
		mapFamilySimilarities.clear();
		mapFamilySimilarities = null;

		// 4- Position similarities from the element similarities
		diffPosition = this.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimBlocks, diffPosition, nextSteps);

		// 5- Input element similarities from the element similarities
		diffInput = this.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimBlocks, diffInput, nextSteps);

		// 6- Calculates the total distance in base of the coefficients
		totalDistance = (diffFamily / DistanceEnum.FAMILY.getValue()) + (diffElements / DistanceEnum.ELEMENT.getValue())
				+ (diffPosition / DistanceEnum.POSITION.getValue()) + (diffInput / DistanceEnum.INPUT.getValue());

		return new SolutionDistance(aim.getId(), diffFamily, diffElements, diffPosition, diffInput, totalDistance, nextSteps);
	}

	/**
	 * Function to get the family distance between two blocks
	 * 
	 * @param aimBlocks
	 * @param originBlocks
	 * @param mapFamilySimilarities
	 * @param mapFamilyDifferences
	 * @param diffFamily
	 * @param nextSteps
	 * @return
	 */
	public double familyDistanceCalculation(List<PedagogicalSoftwareBlockDTO> aimBlocks,
											List<PedagogicalSoftwareBlockDTO> originBlocks,
											Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities,
											Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences, double diffFamily,
											NextStepHint nextSteps) {

		// Checks from the aim side
		for (PedagogicalSoftwareBlockDTO aimBlock : aimBlocks) {

			// 2.1- Checks that this family has not been already checked
			if (!mapFamilySimilarities.containsKey(aimBlock.getElementFamily())
					&& !mapFamilyDifferences.containsKey(aimBlock.getElementFamily())) {

				// 2.1.1- Counts the number of elements of this family existing in the origin
				long countOriginFamilies = originBlocks.stream()
						.filter(c -> c.getElementFamily().equals(aimBlock.getElementFamily())).count();
				// 2.1.2- Adds to the family result
				if (countOriginFamilies == 0) {
					// If there are no similar families, we count all the elements in the aim +
					// the element in the aim that has not been included in the origin
					diffFamily += 1;
					List<PedagogicalSoftwareBlockDTO> tmpFamilyDifferences = aimBlocks.stream()
							.filter(f -> f.getElementFamily().equals(aimBlock.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilyDifferences.put(aimBlock.getElementFamily(), tmpFamilyDifferences);

					//Checks if the help has been requested and then insert the next steps
					if(nextSteps != null){
						//We insert all the elements to add in the next step
						List<artie.common.web.dto.PedagogicalSoftwareBlock> tmpDTOBlockList = tmpFamilyDifferences.stream()
																									.map(fd -> {

																										//Creating the next and previous blocks
																										artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
																										artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

																										if(fd.getNext() != null){
																											nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, fd.getNext().getElementName(),  null);
																										}

																										if(fd.getPrevious() != null){
																											previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, fd.getPrevious().getElementName(),  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, fd.getElementName(), nextBlock);
																									}).collect(Collectors.toList());
						nextSteps.putAddBlocks(tmpDTOBlockList);
					}

				} else {
					// If there are similarities, we add these similarities to the family map
					List<PedagogicalSoftwareBlockDTO> existingBlocks = originBlocks.stream()
							.filter(c -> c.getElementFamily().equals(aimBlock.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilySimilarities.put(aimBlock.getElementFamily(), existingBlocks);
				}
			}
		}

		// Checks from the origin side
		for (PedagogicalSoftwareBlockDTO originBlock : originBlocks) {

			// 3.1- Checks that this family has not been already checked
			if (!mapFamilySimilarities.containsKey(originBlock.getElementFamily())
					&& !mapFamilyDifferences.containsKey(originBlock.getElementFamily())) {

				// 3.1.1- Counts the number of elements of this family existing in the origin
				long countAimFamilies = aimBlocks.stream()
						.filter(c -> c.getElementFamily().equals(originBlock.getElementFamily())).count();
				// 3.1.2- Adds to the family result
				if (countAimFamilies == 0) {
					// If there are no similar families, we count all the elements in the origin +
					// the element in the aim that has not been included in the origin
					diffFamily += 1;
					List<PedagogicalSoftwareBlockDTO> tmpFamilyDifferences = originBlocks.stream()
							.filter(f -> f.getElementFamily().equals(originBlock.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilyDifferences.put(originBlock.getElementFamily(), tmpFamilyDifferences);

					//Checks if the help has been requested and then insert the next steps
					if(nextSteps != null){
						//We insert all the elements to delete in the next step
						List<artie.common.web.dto.PedagogicalSoftwareBlock> tmpDTOBlockList = tmpFamilyDifferences.stream()
																									.map(fd -> {

																										//Creating the next block
																										artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
																										artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

																										if(fd.getNext() != null){
																											nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, fd.getNext().getElementName(),  null);
																										}

																										if(fd.getPrevious() != null){
																											previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, fd.getPrevious().getElementName(),  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, fd.getElementName(), nextBlock);
																									}).collect(Collectors.toList());
						nextSteps.putDeleteBlocks(tmpDTOBlockList);
					}
				}
			}
		}

		return diffFamily;
	}

	/**
	 * Function to get the block distance between two blocks
	 * 
	 * @param mapFamilySimilarities
	 * @param mapBlockSimilarities
	 * @param mapFamilyDifferences
	 * @param aimBlocks
	 * @param diffBlocks
	 * @param nextSteps
	 * @return
	 */
	public double elementDistanceCalculation(Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilySimilarities,
											 Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences,
											 Map<String, List<PedagogicalSoftwareBlockDTO>> mapBlockSimilarities,
											 List<PedagogicalSoftwareBlockDTO> aimBlocks, double diffBlocks,
											 NextStepHint nextSteps) {

		List<String> blocksPassed = new ArrayList<>();

		// Adds the different blocks from the different families to the distance
		// calculation result
		for (String family : mapFamilyDifferences.keySet()) {
			diffBlocks += mapFamilyDifferences.get(family).size();
		}

		// For the similar families
		for (String family : mapFamilySimilarities.keySet()) {

			// Control about the blocks of the family that have been already taken into
			// account
			blocksPassed.clear();

			// 3.1- Gets the blocks in the aim for this family
			List<PedagogicalSoftwareBlockDTO> familyAimBlocks = aimBlocks.stream()
					.filter(c -> c.getElementFamily().equals(family)).collect(Collectors.toList());
			// 3.2- Gets the elements in the origin for this family
			List<PedagogicalSoftwareBlockDTO> familyOriginBlocks = mapFamilySimilarities.get(family);
			List<String> familyOriginTakenAccountBlocksAdd = new ArrayList<>();

			// 3.3- For each aim block we look for the origin block
			for (PedagogicalSoftwareBlockDTO familyAimBlock : familyAimBlocks) {

				// 3.3.1- Counts how many aim blocks are the same block
				List<PedagogicalSoftwareBlockDTO> tmpAimBlocks = familyAimBlocks.stream()
						.filter(c -> c.getElementName().equals(familyAimBlock.getElementName()))
						.collect(Collectors.toList());

				// 3.3.2 - Counts the number of blocks similar to the aim block for the
				// family
				List<PedagogicalSoftwareBlockDTO> tmpOriginBlocks = familyOriginBlocks.stream()
						.filter(c -> c.getElementName().equals(familyAimBlock.getElementName()))
						.collect(Collectors.toList());

				// we check if the block has been already taken into account
				if (!blocksPassed.contains(familyAimBlock.getElementName())) {
					diffBlocks += Math.abs(tmpAimBlocks.size() - tmpOriginBlocks.size());
					blocksPassed.add(familyAimBlock.getElementName());
				}

				// 3.3.3- Adds to the block result
				if (tmpOriginBlocks.size() > 0) {

					int nearestPosition = -1;
					int diffPosition = 0;
					PedagogicalSoftwareBlockDTO nearest = null;
					List<PedagogicalSoftwareBlockDTO> nearestBlocks = new ArrayList<>();

					// For each aim, we insert the nearest origin block in the map
					for (PedagogicalSoftwareBlockDTO tmpAimBlock : tmpAimBlocks) {

						nearestPosition = -1;
						nearest = null;

						//If we want to set the next steps
						if (nextSteps != null) {
							// 3.3.3.1- Checks if we have to add the aim block to the next hints or delete an origin block
							List<PedagogicalSoftwareBlockDTO> listTmpOriginBlocks = tmpOriginBlocks.stream()
									.filter(toe -> toe.getElementName().equals(tmpAimBlock.getElementName()))
									.collect(Collectors.toList());

							// 3.3.3.2- Taking into account the origin blocks that have been deleted before
							listTmpOriginBlocks.addAll(nearestBlocks);

							//3.3.3.3- We have to add the block to the next hint and the element has not been taken into account
							if (listTmpOriginBlocks.size() == 0 && !familyOriginTakenAccountBlocksAdd.contains(familyAimBlock.getElementName())) {
								artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
								artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

								if (tmpAimBlock.getNext() != null) {
									nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, tmpAimBlock.getNext().getElementName(), null);
								}
								if (tmpAimBlock.getPrevious() != null) {
									previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, tmpAimBlock.getPrevious().getElementName(), null);
								}
								nextSteps.putAddBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, tmpAimBlock.getElementName(), nextBlock));
							}
							else {
								//3.3.3.4- We check if the number of blocks with the same name are equals in the origin and the aim
								List<PedagogicalSoftwareBlockDTO> listTmpAimBlocks = tmpAimBlocks.stream()
										.filter(toe -> toe.getElementName().equals(tmpAimBlock.getElementName()))
										.collect(Collectors.toList());
								int blockDifference = Math.abs(listTmpOriginBlocks.size() - listTmpAimBlocks.size());


								if (listTmpOriginBlocks.size() > listTmpAimBlocks.size()) {
									//3.3.3.5- Blocks to be deleted (the farther)
									nextSteps.putDeleteBlocks(
											listTmpOriginBlocks.subList(0, blockDifference).stream().map(toe -> {
												artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
												artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;
												if (toe.getNext() != null) {
													nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, toe.getNext().getElementName(), null);
												}
												if (toe.getPrevious() != null) {
													previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, toe.getPrevious().getElementName(), null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, toe.getElementName(), nextBlock);
											}).collect(Collectors.toList())
									);
								}
								else if (listTmpOriginBlocks.size() < listTmpAimBlocks.size() && !familyOriginTakenAccountBlocksAdd.contains(listTmpAimBlocks.get(0).getElementName())) {
									//3.3.3.6- Blocks to be added
									List<artie.common.web.dto.PedagogicalSoftwareBlock> tmpFilteredList =
											listTmpAimBlocks.subList(0, blockDifference).stream().map(tae -> {
												artie.common.web.dto.PedagogicalSoftwareBlock nextElement = null;
												artie.common.web.dto.PedagogicalSoftwareBlock previousElement = null;
												if (tae.getNext() != null) {
													nextElement = new artie.common.web.dto.PedagogicalSoftwareBlock(null, tae.getNext().getElementName(), null);
												}
												if (tae.getPrevious() != null) {
													previousElement = new artie.common.web.dto.PedagogicalSoftwareBlock(null, tae.getPrevious().getElementName(), null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareBlock(previousElement,tae.getElementName(), nextElement);
											}).collect(Collectors.toList());

									nextSteps.putAddBlocks(tmpFilteredList);
									familyOriginTakenAccountBlocksAdd.addAll(tmpFilteredList.stream().map(fl -> fl.getBlockName()).collect(Collectors.toList()));
								}
							}
						}

						for (PedagogicalSoftwareBlockDTO tmpOriginBlock : tmpOriginBlocks) {

							diffPosition = Math
									.abs(tmpAimBlock.getElementPosition() - tmpOriginBlock.getElementPosition());

							if (nearestPosition == -1) {
								nearestPosition = diffPosition;
								nearest = tmpOriginBlock;
							} else if (nearestPosition > diffPosition) {
								nearestPosition = diffPosition;
								nearest = tmpOriginBlock;
							}
						}

						if (nearest != null) {
							nearestBlocks.add(nearest);
							tmpOriginBlocks.remove(nearest);
						}
					}

					// If there are similarities, we add these similarities to the block map
					mapBlockSimilarities.put(familyAimBlock.getElementName().toLowerCase(), nearestBlocks);

					// We avoid to repeat the same block
					familyOriginBlocks.removeAll(nearestBlocks);
					familyOriginTakenAccountBlocksAdd.addAll(nearestBlocks.stream().map(e -> {return e.getElementName();}).collect(Collectors.toList()));
				}
				//If there are no origin blocks that correspond with the aim,
				// we want to get the next steps and we have not yet taken the block into account
				else if(nextSteps != null && tmpOriginBlocks.size() == 0 && !familyOriginTakenAccountBlocksAdd.contains(familyAimBlock.getElementName())){
					artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
					artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

					if (familyAimBlock.getNext() != null) {
						nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, familyAimBlock.getNext().getElementName(), null);
					}
					if (familyAimBlock.getPrevious() != null) {
						previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, familyAimBlock.getPrevious().getElementName(), null);
					}
					nextSteps.putAddBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, familyAimBlock.getElementName(), nextBlock));
				}
			}

			if(nextSteps != null) {

				//3.4- If we want the next blocks, for each origin element we look for the aim block
				for (PedagogicalSoftwareBlockDTO familyOriginBlock : familyOriginBlocks) {

					//3.4.1- Counts the number of this block in the aim
					long tmpAimBlocks = familyAimBlocks.stream().filter(c -> c.getElementName().equals(familyOriginBlock.getElementName())).count();

					//If there are no blocks in the aim, we have to delete it from the origin
					if(tmpAimBlocks==0){

						artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
						artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

						if (familyOriginBlock.getNext() != null) {
							nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, familyOriginBlock.getNext().getElementName(), null);
						}
						if (familyOriginBlock.getPrevious() != null) {
							previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, familyOriginBlock.getPrevious().getElementName(), null);
						}
						nextSteps.putDeleteBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, familyOriginBlock.getElementName(), nextBlock));
					}
				}
			}

			// 3.5- Once we got all the aim blocks, we check how many blocks of this
			// family remain in the origin and have not been taken into account
			diffBlocks += familyOriginBlocks.stream().filter(b -> !familyOriginTakenAccountBlocksAdd.contains(b.getElementName())).count();
		}

		return diffBlocks;
	}

	/**
	 * Function to get the input distance between the inputs from the same elements
	 * @param mapBlockSimilarities
	 * @param aimBlocks
	 * @param diffInputValues
	 * @param nextSteps
	 * @return
	 */
	public double inputDistanceCalculation(Map<String, List<PedagogicalSoftwareBlockDTO>> mapBlockSimilarities,
										   Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences,
										   List<PedagogicalSoftwareBlockDTO> aimBlocks, double diffInputValues,
										   NextStepHint nextSteps) {
		
		//Adds to the distance calculation result, the different inputs from the difference of the blocks
		for(List<PedagogicalSoftwareBlockDTO> blocks : mapFamilyDifferences.values()) {
			for(PedagogicalSoftwareBlockDTO block : blocks) {
				for(PedagogicalSoftwareInput input : block.getInputs()) {
					for(PedagogicalSoftwareField field : input.getFields()) {
						if(field.isNumeric()) {
							diffInputValues += field.getDoubleValue();
						}else {
							diffInputValues += 1;
						}
					}
				}
			}
		}

		//Create the comparator to sort the blocks by position
		Comparator<PedagogicalSoftwareBlockDTO> compareByElementPosition = (PedagogicalSoftwareBlockDTO b1, PedagogicalSoftwareBlockDTO b2) -> ((Integer)b1.getElementPosition()).compareTo(b2.getElementPosition());
		
		
		//Checks the block similarities
		for(String block : mapBlockSimilarities.keySet()) {
			
			//5.1- Gets the blocks in the aim for this block
			List<PedagogicalSoftwareBlockDTO> blockAimBlocks = aimBlocks
																.stream()
																.filter(c -> c.getElementName().toLowerCase().equals(block.toLowerCase()))
																.sorted(compareByElementPosition)
																.collect(Collectors.toList());
			
			//5.2- Gets the blocks in the origin
			List<PedagogicalSoftwareBlockDTO> blockOriginBlocks = mapBlockSimilarities.get(block.toLowerCase())
					 																	.stream()
																						.map(eoe -> eoe.clone())
																						.sorted(compareByElementPosition)
																						.collect(Collectors.toList());

			//5.3- Checks all the aim blocks
			for(PedagogicalSoftwareBlockDTO blockAimBlock : blockAimBlocks) {

				double nearestDifference = -1;
				PedagogicalSoftwareBlockDTO nearestOrigin = null;
				
				//5.3.1- Checks all the origin block for each aim block
				for	(PedagogicalSoftwareBlockDTO blockOriginBlock : blockOriginBlocks) {
					
					double accumulatedOriginDifference = 0;
					
					//5.3.1.1 - Compares all the inputs for the origin and the aim elements
					for(int input=0; input < blockOriginBlock.getInputs().size(); input++) {
						for(int field=0; field < blockOriginBlock.getInputs().get(input).getFields().size(); field++)
						{
							PedagogicalSoftwareField originField = blockOriginBlock.getInputs().get(input).getFields().get(field);

							//Checks if the field exists in the aim
							PedagogicalSoftwareField aimField = null;
							if(blockAimBlock.getInputs().size() > input && blockAimBlock.getInputs().get(input).getFields().size() > field) {
								aimField = blockAimBlock.getInputs().get(input).getFields().get(field);
							}

							//If the origin field in the origin is numeric
							if(originField.isNumeric())
							{
								//Checks if the aim field is null or not to calculate the difference and the ratio
								double difference = 0;
								double ratio = 0;
								if(aimField != null) {
									difference = Math.abs(originField.getDoubleValue() - aimField.getDoubleValue());
									ratio = (aimField.getDoubleValue() != 0 ? difference / aimField.getDoubleValue() : difference);
								}else{
									difference = Math.abs(originField.getDoubleValue());
									ratio = difference;
								}

								accumulatedOriginDifference += ratio;

								//5.3.1.1.1- Adding the next step hints for double values
								if(nextSteps != null && difference != 0 && blockOriginBlock.getElementPosition() == blockAimBlock.getElementPosition()){
									artie.common.web.dto.PedagogicalSoftwareBlock tmpNextBlock = null;
									artie.common.web.dto.PedagogicalSoftwareBlock tmpPreviousBlock = null;

									if(blockOriginBlock.getNext() != null){
										tmpNextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, blockOriginBlock.getNext().getElementName(), null);
									}
									if(blockOriginBlock.getPrevious() != null){
										tmpPreviousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, blockOriginBlock.getPrevious().getElementName(), null);
									}

									artie.common.web.dto.PedagogicalSoftwareBlock tmpBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(tmpPreviousBlock, blockOriginBlock.getElementName(), tmpNextBlock);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(blockOriginBlock.getInputs().get(input).getName(), originField.getName(),blockOriginBlock.getInputs().get(input).getOpcode(), tmpBlock, Double.toString(originField.getDoubleValue()), Double.toString(aimField.getDoubleValue())));
								}

							}
							//If the value of the origin field is not equal to the aim field value
							else if(aimField == null || !originField.getValue().toLowerCase().equals(aimField.getValue().toLowerCase()))
							{
								accumulatedOriginDifference += 1;

								//5.3.1.1.2-Adding the next step hints for string values
								if(nextSteps != null && blockOriginBlock.getElementPosition() == blockAimBlock.getElementPosition()){

									artie.common.web.dto.PedagogicalSoftwareBlock tmpNextBlock = null;
									artie.common.web.dto.PedagogicalSoftwareBlock tmpPreviousBlock = null;

									if(blockOriginBlock.getNext() != null){
										tmpNextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, blockOriginBlock.getNext().getElementName(), null);
									}
									if(blockOriginBlock.getPrevious() != null){
										tmpPreviousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, blockOriginBlock.getPrevious().getElementName(), null);
									}

									artie.common.web.dto.PedagogicalSoftwareBlock tmpElement = new artie.common.web.dto.PedagogicalSoftwareBlock(tmpPreviousBlock, blockOriginBlock.getElementName(), tmpNextBlock);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(blockOriginBlock.getInputs().get(input).getName(), originField.getName(), blockOriginBlock.getInputs().get(input).getOpcode(), tmpElement, originField.getValue(), aimField.getValue()));
								}
							}
						}
					}
					
					//5.3.1.2 - Checks if the origin block is the nearest block of the aim
					if(nearestDifference == -1 || nearestDifference > accumulatedOriginDifference) {
						nearestDifference = accumulatedOriginDifference;
						nearestOrigin = blockOriginBlock;
					}
				}
				
				//5.4- Deletes the nearest block origin and we add the nearest difference
				if(nearestDifference > -1 && nearestOrigin != null) {
					diffInputValues += nearestDifference;
					blockOriginBlocks.remove(nearestOrigin);
				}
			}
			
		}
		
		return diffInputValues;
	}

	/**
	 * Function to calculate the distance between the positions
	 * 
	 * @param mapBlockSimilarities
	 * @param mapFamilyDifferences
	 * @param aimBlocks
	 * @param diffPosition
	 * @param nextSteps
	 * @return
	 */
	public double positionDistanceCalculation(Map<String, List<PedagogicalSoftwareBlockDTO>> mapBlockSimilarities,
											  Map<String, List<PedagogicalSoftwareBlockDTO>> mapFamilyDifferences,
											  List<PedagogicalSoftwareBlockDTO> aimBlocks,
											  double diffPosition,
											  NextStepHint nextSteps) {

		
		//Adds to the distance calculation result, the different position from the difference of the blocks
		for(List<PedagogicalSoftwareBlockDTO> blocks : mapFamilyDifferences.values()) {
			for(PedagogicalSoftwareBlockDTO block : blocks) {
				diffPosition += block.getElementPosition() + 1;
			}
		}
		
		for (String block : mapBlockSimilarities.keySet()) {

			// 4.1- Gets the elements in the aim for this block
			List<PedagogicalSoftwareBlockDTO> blockAimBlocks = aimBlocks.stream()
					.filter(c -> c.getElementName().toLowerCase().equals(block.toLowerCase())).collect(Collectors.toList());

			// 4.2- Gets the blocks in the origin
			List<PedagogicalSoftwareBlockDTO> elementOriginBlocks = mapBlockSimilarities.get(block.toLowerCase()).stream()
					.map(mes -> mes.clone()).collect(Collectors.toList());

			int nearestPosition = -1;
			int tmpDiff = 0;
			PedagogicalSoftwareBlockDTO nearestBlock = null;

			// 4.3- For each aim block, we look for the nearest origin block
			for (PedagogicalSoftwareBlockDTO aimBlock : blockAimBlocks) {

				if (elementOriginBlocks.size() > 0) {

					for (PedagogicalSoftwareBlockDTO originBlock : elementOriginBlocks) {

						tmpDiff = Math.abs(aimBlock.getElementPosition() - originBlock.getElementPosition());
						if (nearestPosition == -1) {
							nearestPosition = tmpDiff;
							nearestBlock = originBlock;
						} else if (nearestPosition > tmpDiff) {
							nearestPosition = tmpDiff;
							nearestBlock = originBlock;
						}
					}

					//If the help is requested
					if(nextSteps != null && aimBlock.getElementPosition() != nearestBlock.getElementPosition()){
						artie.common.web.dto.PedagogicalSoftwareBlock nextBlock = null;
						artie.common.web.dto.PedagogicalSoftwareBlock previousBlock = null;

						if(nearestBlock.getNext() != null){
							nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, nearestBlock.getNext().getElementName(), null);
						}
						if(nearestBlock.getPrevious() != null){
							previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(null, nearestBlock.getPrevious().getElementName(), null);
						}

						nextSteps.putReplacePositions(new artie.common.web.dto.PedagogicalSoftwareBlock(previousBlock, nearestBlock.getElementName(), nextBlock));
					}
					diffPosition += nearestPosition;
					elementOriginBlocks.remove(nearestBlock);

				} else {
					diffPosition += aimBlock.getElementPosition() + 1;
				}

				nearestPosition = -1;
			}

		}

		return diffPosition;
	}

	/**
	 * Function to get all the blocks in a single list
	 * 
	 * @param block     block to analyze its position
	 * @param blockList cumulative block list
	 * @param position    cumulative position
	 * @return
	 */
	public List<PedagogicalSoftwareBlockDTO> getAllElements(PedagogicalSoftwareBlock block,
															List<PedagogicalSoftwareBlockDTO> blockList, AtomicInteger position) {

		// Adds the block to the list
		blockList.add(new PedagogicalSoftwareBlockDTO(block, position.get()));
		position.incrementAndGet();

		int numberOfSubBlocks = 0;

		// Checks if the block has a nested block
		for (PedagogicalSoftwareBlock nestedBlock : block.getNested()) {

			// Gets the number of blocks of the nested block
			numberOfSubBlocks = getBlocksUnderNode(nestedBlock, 0);

			// We add 1 because it's a nested block
			position.incrementAndGet();
			position.getAndAdd(numberOfSubBlocks);
			blockList = this.getAllElements(nestedBlock, blockList, position);
		}

		// Checks if the block has a next block
		if (block.getNext() != null) {

			// Gets the next elements
			blockList = this.getAllElements(block.getNext(), blockList, position);
		}

		return blockList;
	}

	/**
	 * Function to get the number of blocks of a node
	 * 
	 * @param block
	 * @param subBlocks
	 * @return
	 */
	private int getBlocksUnderNode(PedagogicalSoftwareBlock block, int subBlocks) {

		// 1- Counts all the nested blocks in the subtree
		if (block.getNested().size() > 0) {
			for (PedagogicalSoftwareBlock nestedElement : block.getNested()) {
				subBlocks++;
				subBlocks = getBlocksUnderNode(nestedElement, subBlocks);
			}
		}

		// 2- Counts all the next blocks in the subtree
		if (block.getNext() != null) {
			subBlocks++;
			subBlocks = getBlocksUnderNode(block.getNext(), subBlocks);
		}

		return subBlocks;
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
	 * @param studentId
	 * @param exerciseId
	 * @return
	 */
	public List<LearningProgress> findByStudentAndExercise(String studentId, String exerciseId){

		//Gets all the pedagogical software data of the student and the selected exercise
		List<PedagogicalSoftwareData> pedagogicalSoftwareDataList = this.pedagogicalSoftwareDataRepository.findByStudent_IdAndExerciseId(studentId, exerciseId);

		//Transforms this information in a learning progress list
		List<LearningProgress> learningProgressList = pedagogicalSoftwareDataList.stream().map(ps -> {
			return new LearningProgress(ps.getExercise(), ps.getStudent(), ps.getSolutionDistance().getTotalDistance(), ps.getGrade(),
										ps.getDateTime(), ps.getLastLogin(), ps.isRequestHelp(), ps.getSecondsHelpOpen(), ps.isFinishedExercise(),
										ps.getValidSolution());
		}).collect(Collectors.toList());

		return learningProgressList;
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
				SolutionDistance solutionDistance = this.distanceCalculation(psd, pss);

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

package artie.pedagogicalintervention.webservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import artie.common.web.dto.Exercise;
import artie.common.web.dto.NextStepHint;
import artie.common.web.dto.Response;
import artie.common.web.dto.ResponseBody;
import artie.common.web.enums.ValidSolutionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareBlockDTO;
import artie.pedagogicalintervention.webservice.enums.DistanceEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareDistance;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareField;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;

@Service
public class PedagogicalSoftwareService {

	@Autowired
	private PedagogicalSoftwareDataRepository pedagogicalSoftwareDataRepository;

	@Autowired
	private PedagogicalSoftwareSolutionService pedagogicalSoftwareSolutionService;

	/**
	 * Function to add the pedagogical software data in the database
	 * 
	 * @param pedagogicalSoftwareData
	 */
	public String add(PedagogicalSoftwareData pedagogicalSoftwareData) {

		Response response = new Response(null);

		// 1- Looks for the solution to the exercise
		List<PedagogicalSoftwareSolution> pedagogicalSoftwareSolution = this.pedagogicalSoftwareSolutionService.findByExerciseAndUserId(pedagogicalSoftwareData.getExercise(), pedagogicalSoftwareData.getStudent().getUserId());

		// 2- If there at least 1 solution, we get the distances
		PedagogicalSoftwareDistance distance = null;
		if (pedagogicalSoftwareSolution != null) {
			distance = this.distanceCalculation(pedagogicalSoftwareData, pedagogicalSoftwareSolution);
			pedagogicalSoftwareData.setSolutionDistance(distance);
		}

		PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);

		//3- Creating the return object
		if (objSaved != null && pedagogicalSoftwareData.getRequestHelp() == false && distance != null) {
			//3.1- If we haven't requested any kind of help, but we have a distance
			response = new Response(new ResponseBody(distance));
		}else if(objSaved != null && pedagogicalSoftwareData.getRequestHelp() == false){
			//3.2- If we haven't requested any kind of help, we just return if the element has been saved or not
			response = new Response(new ResponseBody("OK"));
		}else if(pedagogicalSoftwareData.getRequestHelp() && distance != null){
			//3.3- If we have requested help, we return the next hints
			response = new Response(new ResponseBody(distance));
		}else if(pedagogicalSoftwareData.getRequestHelp() && distance == null){
			//3.4- If the distance is null and we have requested help, there must be an error
			response = new Response(new ResponseBody("ERROR"));
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

			// 1- Transforms the string into the pedagogical software data
			PedagogicalSoftwareData pedagogicalSoftwareData = new ObjectMapper().readValue(psd,
					PedagogicalSoftwareData.class);

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
																			return new Exercise(e.getId(), e.getExercise().getName(), e.getExerciseId(), e.getExercise().getDescription(), e.getScreenShot(), e.getValidSolution());
																		})
																		.collect(Collectors.toList());

		return listFinishedExercises;
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
				pedagogicalSoftwareData.setSolutionDistance(new PedagogicalSoftwareDistance(0,0,0,0,0, null));

				//We register the new solution
				this.pedagogicalSoftwareSolutionService.addFromPedagogicalSoftwareDataId(pedagogicalDataId);
			}else{

				//we delete the solution
				this.pedagogicalSoftwareSolutionService.deleteFromPedagogicalSoftwareDataId(pedagogicalDataId);

				//We calculate the distance of the pedagogical software data
				List<PedagogicalSoftwareSolution> listSolutions = this.pedagogicalSoftwareSolutionService.findByExerciseAndUserId(pedagogicalSoftwareData.getExercise(), pedagogicalSoftwareData.getStudent().getUserId());
				PedagogicalSoftwareDistance pedagogicalSoftwareDistance = this.distanceCalculation(pedagogicalSoftwareData, listSolutions);
				pedagogicalSoftwareData.setSolutionDistance(pedagogicalSoftwareDistance);
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
	public PedagogicalSoftwareDistance distanceCalculation(PedagogicalSoftwareData origin, List<PedagogicalSoftwareSolution> aims){

		PedagogicalSoftwareDistance nearestDistance = null;

		//1- Gets the distance between all the solutions
		for(PedagogicalSoftwareSolution aim : aims){
			PedagogicalSoftwareDistance distance = this.distanceCalculation(origin, aim);

			//2- Sets the nearest distance
			if(nearestDistance == null || distance.getTotalDistance() < nearestDistance.getTotalDistance()){
				nearestDistance = distance;
			}
		}

		return nearestDistance;
	}

	/**
	 * Distance calculation between an element and its aim
	 * 
	 * @param origin
	 * @param aim
	 * @return
	 */
	public PedagogicalSoftwareDistance distanceCalculation(PedagogicalSoftwareData origin, PedagogicalSoftwareSolution aim) {

		List<PedagogicalSoftwareBlockDTO> aimBlocks = new ArrayList<>();
		List<PedagogicalSoftwareBlockDTO> originBlocks = new ArrayList<>();

		//Preparing the next steps in base if the user has requested help or not
		NextStepHint nextSteps = (origin.getRequestHelp() ? new NextStepHint() : null);

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

		return new PedagogicalSoftwareDistance(diffFamily, diffElements, diffPosition, diffInput, totalDistance, nextSteps);
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
																											nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getNext().getElementName(), null,  null);
																										}

																										if(fd.getPrevious() != null){
																											previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getPrevious().getElementName(), null,  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getElementName(), previousBlock, nextBlock);
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
																											nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getNext().getElementName(), null,  null);
																										}

																										if(fd.getPrevious() != null){
																											previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getPrevious().getElementName(), null,  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareBlock(fd.getElementName(), previousBlock, nextBlock);
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
									nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(tmpAimBlock.getNext().getElementName(), null, null);
								}
								if (tmpAimBlock.getPrevious() != null) {
									previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(tmpAimBlock.getPrevious().getElementName(), null, null);
								}
								nextSteps.putAddBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(tmpAimBlock.getElementName(), previousBlock, nextBlock));
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
													nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(toe.getNext().getElementName(), null, null);
												}
												if (toe.getPrevious() != null) {
													previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(toe.getPrevious().getElementName(), null, null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareBlock(toe.getElementName(), previousBlock, nextBlock);
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
													nextElement = new artie.common.web.dto.PedagogicalSoftwareBlock(tae.getNext().getElementName(), null, null);
												}
												if (tae.getPrevious() != null) {
													previousElement = new artie.common.web.dto.PedagogicalSoftwareBlock(tae.getPrevious().getElementName(), null, null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareBlock(tae.getElementName(), previousElement, nextElement);
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
					mapBlockSimilarities.put(familyAimBlock.getElementName(), nearestBlocks);

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
						nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(familyAimBlock.getNext().getElementName(), null, null);
					}
					if (familyAimBlock.getPrevious() != null) {
						previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(familyAimBlock.getPrevious().getElementName(), null, null);
					}
					nextSteps.putAddBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(familyAimBlock.getElementName(), previousBlock, nextBlock));
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
							nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(familyOriginBlock.getNext().getElementName(), null, null);
						}
						if (familyOriginBlock.getPrevious() != null) {
							previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(familyOriginBlock.getPrevious().getElementName(), null, null);
						}
						nextSteps.putDeleteBlocks(new artie.common.web.dto.PedagogicalSoftwareBlock(familyOriginBlock.getElementName(), previousBlock, nextBlock));
					}
				}
			}

			// 3.5- Once we got all the aim blocks, we check how many blocks of this
			// family remain in the origin
			diffBlocks += familyOriginBlocks.size();
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
		
		
		//Checks the block similarities
		for(String block : mapBlockSimilarities.keySet()) {
			
			//5.1- Gets the blocks in the aim for this block
			List<PedagogicalSoftwareBlockDTO> blockAimBlocks = aimBlocks
																.stream()
																.filter(c -> c.getElementName().equals(block))
																.collect(Collectors.toList());
			
			//5.2- Gets the blocks in the origin
			List<PedagogicalSoftwareBlockDTO> blockOriginBlocks = mapBlockSimilarities.get(block).stream().map(eoe -> eoe.clone()).collect(Collectors.toList());

			//List to avoid insert many times the same block for the help hints
			//Here we will insert the origin blocks to know if exactly if an block has been added or not
			List<PedagogicalSoftwareBlockDTO> blocksAlreadyAddedForHints = new ArrayList<>();

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
							PedagogicalSoftwareField aimField = blockAimBlock.getInputs().get(input).getFields().get(field);
							
							if(originField.isNumeric()) {
								double difference = Math.abs(originField.getDoubleValue() - aimField.getDoubleValue());
								double ratio = difference / aimField.getDoubleValue();
								accumulatedOriginDifference += ratio;

								//5.3.1.1.1-Adding the next step hints for double values
								if(nextSteps != null && difference != 0 && !blocksAlreadyAddedForHints.contains(blockOriginBlock)){
									artie.common.web.dto.PedagogicalSoftwareBlock tmpNextBlock = null;
									artie.common.web.dto.PedagogicalSoftwareBlock tmpPreviousBlock = null;

									if(blockOriginBlock.getNext() != null){
										tmpNextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getNext().getElementName(), null, null);
									}
									if(blockOriginBlock.getPrevious() != null){
										tmpPreviousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getPrevious().getElementName(), null, null);
									}

									artie.common.web.dto.PedagogicalSoftwareBlock tmpBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getElementName(), tmpPreviousBlock, tmpNextBlock);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(blockOriginBlock.getInputs().get(input).getName(), originField.getName(),blockOriginBlock.getInputs().get(input).getOpCode(), tmpBlock, Double.toString(originField.getDoubleValue()), Double.toString(aimField.getDoubleValue())));
									blocksAlreadyAddedForHints.add(blockOriginBlock);
								}

							}else if(!originField.getValue().equals(aimField.getValue())) {
								accumulatedOriginDifference += 1;

								//5.3.1.1.2-Adding the next step hints for string values
								if(nextSteps != null){
									artie.common.web.dto.PedagogicalSoftwareBlock tmpNextBlock = null;
									artie.common.web.dto.PedagogicalSoftwareBlock tmpPreviousBlock = null;

									if(blockOriginBlock.getNext() != null){
										tmpNextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getNext().getElementName(), null, null);
									}
									if(blockOriginBlock.getPrevious() != null){
										tmpPreviousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getPrevious().getElementName(), null, null);
									}

									artie.common.web.dto.PedagogicalSoftwareBlock tmpElement = new artie.common.web.dto.PedagogicalSoftwareBlock(blockOriginBlock.getElementName(), tmpPreviousBlock,tmpNextBlock);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(blockOriginBlock.getInputs().get(input).getName(), originField.getName(), blockOriginBlock.getInputs().get(input).getOpCode(), tmpElement, originField.getValue(), aimField.getValue()));
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
					.filter(c -> c.getElementName().equals(block)).collect(Collectors.toList());

			// 4.2- Gets the blocks in the origin
			List<PedagogicalSoftwareBlockDTO> elementOriginBlocks = mapBlockSimilarities.get(block).stream()
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
							nextBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(nearestBlock.getNext().getElementName(), null, null);
						}
						if(nearestBlock.getPrevious() != null){
							previousBlock = new artie.common.web.dto.PedagogicalSoftwareBlock(nearestBlock.getPrevious().getElementName(), null, null);
						}

						nextSteps.putReplacePositions(new artie.common.web.dto.PedagogicalSoftwareBlock(nearestBlock.getElementName(), previousBlock, nextBlock));
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
}

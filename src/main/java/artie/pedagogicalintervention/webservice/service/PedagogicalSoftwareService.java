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

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.enums.DistanceEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareDistance;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
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
		if (objSaved != null && pedagogicalSoftwareData.getRequestHelp() == false) {
			//3.1- If we haven't requested any kind of help, we just return if the element has been saved or not
			response = new Response(new ResponseBody("OK"));
		}else if(pedagogicalSoftwareData.getRequestHelp() && distance != null){
			//3.2- If we have requested help, we return the next hints
			response = new Response(new ResponseBody(distance));
		}else if(pedagogicalSoftwareData.getRequestHelp() && distance == null){
			//3.3- If the distance is null and we have requested help, there must be an error
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

		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();

		//Preparing the next steps in base if the user has requested help or not
		NextStepHint nextSteps = (origin.getRequestHelp() ? new NextStepHint() : null);

		// Family variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences = new HashMap<>();
		double diffFamily = 0;

		// Element variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities = new HashMap<>();
		double diffElements = 0;

		// Position variables
		double diffPosition = 0;

		// Input values variables
		double diffInput = 0;

		// total distance
		double totalDistance = 0;

		// 1- Getting all the elements in a single list (not nested)
		for (PedagogicalSoftwareElement element : aim.getElements()) {
			aimElements = this.getAllElements(element, aimElements, new AtomicInteger(0));
		}
		for (PedagogicalSoftwareElement element : origin.getElements()) {
			originElements = this.getAllElements(element, originElements, new AtomicInteger(0));

		}

		// 2- Family differences and similarities
		diffFamily = this.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, diffFamily, nextSteps);

		// 3- Element similarities from the family similarities
		diffElements = this.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, diffElements, nextSteps);

		// We can now delete the family similarities map
		mapFamilySimilarities.clear();
		mapFamilySimilarities = null;

		// 4- Position similarities from the element similarities
		diffPosition = this.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, diffPosition);

		// 5- Input element similarities from the element similarities
		diffInput = this.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, diffInput, nextSteps);

		// 6- Calculates the total distance in base of the coefficients
		totalDistance = (diffFamily / DistanceEnum.FAMILY.getValue()) + (diffElements / DistanceEnum.ELEMENT.getValue())
				+ (diffPosition / DistanceEnum.POSITION.getValue()) + (diffInput / DistanceEnum.INPUT.getValue());

		return new PedagogicalSoftwareDistance(diffFamily, diffElements, diffPosition, diffInput, totalDistance, nextSteps);
	}

	/**
	 * Function to get the family distance between two elements
	 * 
	 * @param aimElements
	 * @param originElements
	 * @param mapFamilySimilarities
	 * @param mapFamilyDifferences
	 * @param diffFamily
	 * @param nextSteps
	 * @return
	 */
	public double familyDistanceCalculation(List<PedagogicalSoftwareElementDTO> aimElements,
			List<PedagogicalSoftwareElementDTO> originElements,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences, double diffFamily,
			NextStepHint nextSteps) {

		// Checks from the aim side
		for (PedagogicalSoftwareElementDTO aimElement : aimElements) {

			// 2.1- Checks that this family has not been already checked
			if (!mapFamilySimilarities.containsKey(aimElement.getElementFamily())
					&& !mapFamilyDifferences.containsKey(aimElement.getElementFamily())) {

				// 2.1.1- Counts the number of elements of this family existing in the origin
				long countOriginFamilies = originElements.stream()
						.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily())).count();
				// 2.1.2- Adds to the family result
				if (countOriginFamilies == 0) {
					// If there are no similar families, we count all the elements in the aim +
					// the element in the aim that has not been included in the origin
					diffFamily += 1;
					List<PedagogicalSoftwareElementDTO> tmpFamilyDifferences = aimElements.stream()
							.filter(f -> f.getElementFamily().equals(aimElement.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilyDifferences.put(aimElement.getElementFamily(), tmpFamilyDifferences);

					//Checks if the help has been requested and then insert the next steps
					if(nextSteps != null){
						//We insert all the elements to add in the next step
						List<artie.common.web.dto.PedagogicalSoftwareElement> tmpDTOElementList = tmpFamilyDifferences.stream()
																									.map(fd -> {

																										//Creating the next and previous elements
																										artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
																										artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;

																										if(fd.getNext() != null){
																											nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(fd.getNext().getElementName(), null,  null);
																										}

																										if(fd.getPrevious() != null){
																											previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(fd.getPrevious().getElementName(), null,  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareElement(fd.getElementName(), previousElement, nextElement);
																									}).collect(Collectors.toList());
						nextSteps.putAddElements(tmpDTOElementList);
					}

				} else {
					// If there are similarities, we add these similarities to the family map
					List<PedagogicalSoftwareElementDTO> existingElements = originElements.stream()
							.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilySimilarities.put(aimElement.getElementFamily(), existingElements);
				}
			}
		}

		// Checks from the origin side
		for (PedagogicalSoftwareElementDTO originElement : originElements) {

			// 3.1- Checks that this family has not been already checked
			if (!mapFamilySimilarities.containsKey(originElement.getElementFamily())
					&& !mapFamilyDifferences.containsKey(originElement.getElementFamily())) {

				// 3.1.1- Counts the number of elements of this family existing in the origin
				long countAimFamilies = aimElements.stream()
						.filter(c -> c.getElementFamily().equals(originElement.getElementFamily())).count();
				// 3.1.2- Adds to the family result
				if (countAimFamilies == 0) {
					// If there are no similar families, we count all the elements in the origin +
					// the element in the aim that has not been included in the origin
					diffFamily += 1;
					List<PedagogicalSoftwareElementDTO> tmpFamilyDifferences = originElements.stream()
							.filter(f -> f.getElementFamily().equals(originElement.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilyDifferences.put(originElement.getElementFamily(), tmpFamilyDifferences);

					//Checks if the help has been requested and then insert the next steps
					if(nextSteps != null){
						//We insert all the elements to delete in the next step
						List<artie.common.web.dto.PedagogicalSoftwareElement> tmpDTOElementList = tmpFamilyDifferences.stream()
																									.map(fd -> {

																										//Creating the next element
																										artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
																										artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;

																										if(fd.getNext() != null){
																											nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(fd.getNext().getElementName(), null,  null);
																										}

																										if(fd.getPrevious() != null){
																											previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(fd.getPrevious().getElementName(), null,  null);
																										}

																										return new artie.common.web.dto.PedagogicalSoftwareElement(fd.getElementName(), previousElement, nextElement);
																									}).collect(Collectors.toList());
						nextSteps.putDeleteElements(tmpDTOElementList);
					}
				}
			}
		}

		return diffFamily;
	}

	/**
	 * Function to get the element distance between two elements
	 * 
	 * @param mapFamilySimilarities
	 * @param mapElementSimilarities
	 * @param mapFamilyDifferences
	 * @param aimElements
	 * @param diffElements
	 * @param nextSteps
	 * @return
	 */
	public double elementDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities,
											 Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences,
											 Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities,
											 List<PedagogicalSoftwareElementDTO> aimElements, double diffElements,
											 NextStepHint nextSteps) {

		List<String> elementsPassed = new ArrayList<>();

		// Adds the different elements from the different families to the distance
		// calculation result
		for (String family : mapFamilyDifferences.keySet()) {
			diffElements += mapFamilyDifferences.get(family).size();
		}

		// For the similar families
		for (String family : mapFamilySimilarities.keySet()) {

			// Control about the elements of the family that have been already taken into
			// account
			elementsPassed.clear();

			// 3.1- Gets the elements in the aim for this family
			List<PedagogicalSoftwareElementDTO> familyAimElements = aimElements.stream()
					.filter(c -> c.getElementFamily().equals(family)).collect(Collectors.toList());
			// 3.2- Gets the elements in the origin for this family
			List<PedagogicalSoftwareElementDTO> familyOriginElements = mapFamilySimilarities.get(family);
			List<String> familyOriginTakenAccountElementsAdd = new ArrayList<>();

			// 3.3- For each aim element we look for the origin element
			for (PedagogicalSoftwareElementDTO familyAimElement : familyAimElements) {

				// 3.3.1- Counts how many aim elements are the same element
				List<PedagogicalSoftwareElementDTO> tmpAimElements = familyAimElements.stream()
						.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
						.collect(Collectors.toList());

				// 3.3.2 - Counts the number of elements similar to the aim element for the
				// family
				List<PedagogicalSoftwareElementDTO> tmpOriginElements = familyOriginElements.stream()
						.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
						.collect(Collectors.toList());

				// we check if the element has been already taken into account
				if (!elementsPassed.contains(familyAimElement.getElementName())) {
					diffElements += Math.abs(tmpAimElements.size() - tmpOriginElements.size());
					elementsPassed.add(familyAimElement.getElementName());
				}

				// 3.3.3- Adds to the element result
				if (tmpOriginElements.size() > 0) {

					int nearestPosition = -1;
					int diffPosition = 0;
					PedagogicalSoftwareElementDTO nearest = null;
					List<PedagogicalSoftwareElementDTO> nearestElements = new ArrayList<>();

					// For each aim, we insert the nearest origin element in the map
					for (PedagogicalSoftwareElementDTO tmpAimElement : tmpAimElements) {

						nearestPosition = -1;
						nearest = null;

						//If we want to set the next steps
						if (nextSteps != null) {
							// 3.3.3.1- Checks if we have to add the aim element to the next hints or delete an origin element
							List<PedagogicalSoftwareElementDTO> listTmpOriginElements = tmpOriginElements.stream()
									.filter(toe -> toe.getElementName().equals(tmpAimElement.getElementName()))
									.collect(Collectors.toList());

							// 3.3.3.2- Taking into account the origin elements that have been deleted before
							listTmpOriginElements.addAll(nearestElements);

							//3.3.3.3- We have to add the element to the next hint and the element has not been taken into account
							if (listTmpOriginElements.size() == 0 && !familyOriginTakenAccountElementsAdd.contains(familyAimElement.getElementName())) {
								artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
								artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;

								if (tmpAimElement.getNext() != null) {
									nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(tmpAimElement.getNext().getElementName(), null, null);
								}
								if (tmpAimElement.getPrevious() != null) {
									previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(tmpAimElement.getPrevious().getElementName(), null, null);
								}
								nextSteps.putAddElements(new artie.common.web.dto.PedagogicalSoftwareElement(tmpAimElement.getElementName(), previousElement, nextElement));
							}
							else {
								//3.3.3.4- We check if the number of elements with the same name are equals in the origin and the aim
								List<PedagogicalSoftwareElementDTO> listTmpAimElements = tmpAimElements.stream()
										.filter(toe -> toe.getElementName().equals(tmpAimElement.getElementName()))
										.collect(Collectors.toList());
								int elementDifference = Math.abs(listTmpOriginElements.size() - listTmpAimElements.size());


								if (listTmpOriginElements.size() > listTmpAimElements.size()) {
									//3.3.3.5- Elements to be deleted (the farther)
									nextSteps.putDeleteElements(
											listTmpOriginElements.subList(0, elementDifference).stream().map(toe -> {
												artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
												artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;
												if (toe.getNext() != null) {
													nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(toe.getNext().getElementName(), null, null);
												}
												if (toe.getPrevious() != null) {
													previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(toe.getPrevious().getElementName(), null, null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareElement(toe.getElementName(), previousElement, nextElement);
											}).collect(Collectors.toList())
									);
								}
								else if (listTmpOriginElements.size() < listTmpAimElements.size() && !familyOriginTakenAccountElementsAdd.contains(listTmpAimElements.get(0).getElementName())) {
									//3.3.3.6- Elements to be added
									List<artie.common.web.dto.PedagogicalSoftwareElement> tmpFilteredList =
											listTmpAimElements.subList(0, elementDifference).stream().map(tae -> {
												artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
												artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;
												if (tae.getNext() != null) {
													nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(tae.getNext().getElementName(), null, null);
												}
												if (tae.getPrevious() != null) {
													previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(tae.getPrevious().getElementName(), null, null);
												}
												return new artie.common.web.dto.PedagogicalSoftwareElement(tae.getElementName(), previousElement, nextElement);
											}).collect(Collectors.toList());

									nextSteps.putAddElements(tmpFilteredList);
									familyOriginTakenAccountElementsAdd.addAll(tmpFilteredList.stream().map(fl -> fl.getElementName()).collect(Collectors.toList()));
								}
							}
						}

						for (PedagogicalSoftwareElementDTO tmpOriginElement : tmpOriginElements) {

							diffPosition = Math
									.abs(tmpAimElement.getElementPosition() - tmpOriginElement.getElementPosition());

							if (nearestPosition == -1) {
								nearestPosition = diffPosition;
								nearest = tmpOriginElement;
							} else if (nearestPosition > diffPosition) {
								nearestPosition = diffPosition;
								nearest = tmpOriginElement;
							}
						}

						if (nearest != null) {
							nearestElements.add(nearest);
							tmpOriginElements.remove(nearest);
						}
					}

					// If there are similarities, we add these similarities to the element map
					mapElementSimilarities.put(familyAimElement.getElementName(), nearestElements);

					// We avoid to repeat the same element
					familyOriginElements.removeAll(nearestElements);
					familyOriginTakenAccountElementsAdd.addAll(nearestElements.stream().map(e -> {return e.getElementName();}).collect(Collectors.toList()));
				}
				//If there are no origin elements that correspond with the aim,
				// we want to get the next steps and we have not yet taken the element into account
				else if(nextSteps != null && tmpOriginElements.size() == 0 && !familyOriginTakenAccountElementsAdd.contains(familyAimElement.getElementName())){
					artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
					artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;

					if (familyAimElement.getNext() != null) {
						nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(familyAimElement.getNext().getElementName(), null, null);
					}
					if (familyAimElement.getPrevious() != null) {
						previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(familyAimElement.getPrevious().getElementName(), null, null);
					}
					nextSteps.putAddElements(new artie.common.web.dto.PedagogicalSoftwareElement(familyAimElement.getElementName(), previousElement, nextElement));
				}
			}

			if(nextSteps != null) {

				//3.4- If we want the next elements, for each origin element we look for the aim element
				for (PedagogicalSoftwareElementDTO familyOriginElement : familyOriginElements) {

					//3.4.1- Counts the number of this element in the aim
					long tmpAimElements = familyAimElements.stream().filter(c -> c.getElementName().equals(familyOriginElement.getElementName())).count();

					//If there are no elements in the aim, we have to delete it from the origin
					if(tmpAimElements==0){

						artie.common.web.dto.PedagogicalSoftwareElement nextElement = null;
						artie.common.web.dto.PedagogicalSoftwareElement previousElement = null;

						if (familyOriginElement.getNext() != null) {
							nextElement = new artie.common.web.dto.PedagogicalSoftwareElement(familyOriginElement.getNext().getElementName(), null, null);
						}
						if (familyOriginElement.getPrevious() != null) {
							previousElement = new artie.common.web.dto.PedagogicalSoftwareElement(familyOriginElement.getPrevious().getElementName(), null, null);
						}
						nextSteps.putDeleteElements(new artie.common.web.dto.PedagogicalSoftwareElement(familyOriginElement.getElementName(), previousElement, nextElement));
					}
				}
			}

			// 3.5- Once we got all the aim elements, we check how many elements of this
			// family remain in the origin
			diffElements += familyOriginElements.size();
		}

		return diffElements;
	}

	/**
	 * Function to get the input distance between the inputs from the same elements
	 * @param mapElementSimilarities
	 * @param aimElements
	 * @param diffInputValues
	 * @return
	 */
	public double inputDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities,
										   Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences,
										   List<PedagogicalSoftwareElementDTO> aimElements, double diffInputValues,
										   NextStepHint nextSteps) {
		
		//Adds to the distance calculation result, the different inputs from the difference of the elements
		for(List<PedagogicalSoftwareElementDTO> elements : mapFamilyDifferences.values()) {
			for(PedagogicalSoftwareElementDTO element : elements) {
				for(PedagogicalSoftwareInput input : element.getInputs()) {
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
		
		
		//Checks the element similarities
		for(String element : mapElementSimilarities.keySet()) {
			
			//5.1- Gets the elements in the aim for this element
			List<PedagogicalSoftwareElementDTO> elementAimElements = aimElements
																		.stream()
																		.filter(c -> c.getElementName().equals(element))
																		.collect(Collectors.toList());
			
			//5.2- Gets the elements in the origin
			List<PedagogicalSoftwareElementDTO> elementOriginElements = mapElementSimilarities.get(element).stream().map(eoe -> eoe.clone()).collect(Collectors.toList());
			
			//5.3- Checks all the aim elements
			for(PedagogicalSoftwareElementDTO elementAimElement : elementAimElements) {
				
				double nearestDifference = -1;
				PedagogicalSoftwareElementDTO nearestOrigin = null;
				
				//5.3.1- Checks all the origin element for each aim element
				for	(PedagogicalSoftwareElementDTO elementOriginElement : elementOriginElements) {
					
					double accumulatedOriginDifference = 0;
					
					//5.3.1.1 - Compares all the inputs for the origin and the aim elements
					for(int input=0; input < elementOriginElement.getInputs().size(); input++) {
						for(int field=0; field < elementOriginElement.getInputs().get(input).getFields().size(); field++)
						{
							PedagogicalSoftwareField originField = elementOriginElement.getInputs().get(input).getFields().get(field);
							PedagogicalSoftwareField aimField = elementAimElement.getInputs().get(input).getFields().get(field);
							
							if(originField.isNumeric()) {
								double difference = Math.abs(originField.getDoubleValue() - aimField.getDoubleValue());
								double ratio = difference / aimField.getDoubleValue();
								accumulatedOriginDifference += ratio;

								//5.3.1.1.1-Adding the next step hints for double values
								if(nextSteps != null && difference != 0){
									artie.common.web.dto.PedagogicalSoftwareElement tmpNextElement = null;
									artie.common.web.dto.PedagogicalSoftwareElement tmpPreviousElement = null;

									if(elementOriginElement.getNext() != null){
										tmpNextElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getNext().getElementName(), null, null);
									}
									if(elementOriginElement.getPrevious() != null){
										tmpPreviousElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getPrevious().getElementName(), null, null);
									}

									artie.common.web.dto.PedagogicalSoftwareElement tmpElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getElementName(), tmpPreviousElement, tmpNextElement);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(originField.getName(),elementOriginElement.getInputs().get(input).getOpCode(), tmpElement, Double.toString(originField.getDoubleValue()), Double.toString(aimField.getDoubleValue())));
								}

							}else if(!originField.getValue().equals(aimField.getValue())) {
								accumulatedOriginDifference += 1;

								//5.3.1.1.2-Adding the next step hints for string values
								if(nextSteps != null){
									artie.common.web.dto.PedagogicalSoftwareElement tmpNextElement = null;
									artie.common.web.dto.PedagogicalSoftwareElement tmpPreviousElement = null;

									if(elementOriginElement.getNext() != null){
										tmpNextElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getNext().getElementName(), null, null);
									}
									if(elementOriginElement.getPrevious() != null){
										tmpPreviousElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getPrevious().getElementName(), null, null);
									}

									artie.common.web.dto.PedagogicalSoftwareElement tmpElement = new artie.common.web.dto.PedagogicalSoftwareElement(elementOriginElement.getElementName(), tmpPreviousElement,tmpNextElement);
									nextSteps.putReplaceInputs(new artie.common.web.dto.PedagogicalSoftwareInput(originField.getName(), elementOriginElement.getInputs().get(input).getOpCode(), tmpElement, originField.getValue(), aimField.getValue()));
								}
							}
						}
					}
					
					//5.3.1.2 - Checks if the origin element is the nearest element of the aim
					if(nearestDifference == -1 || nearestDifference > accumulatedOriginDifference) {
						nearestDifference = accumulatedOriginDifference;
						nearestOrigin = elementOriginElement;
					}
					
				}
				
				//5.4- Deletes the nearest element origin and we add the nearest difference 
				if(nearestDifference > -1 && nearestOrigin != null) {
					diffInputValues += nearestDifference;
					elementOriginElements.remove(nearestOrigin);
				}
			}
			
		}
		
		return diffInputValues;
	}

	/**
	 * Function to calculate the distance between the positions
	 * 
	 * @param mapElementSimilarities
	 * @param aimElements
	 * @param diffPosition
	 * @return
	 */
	public double positionDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, 
											  Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences, 
											  List<PedagogicalSoftwareElementDTO> aimElements, 
											  double diffPosition) {

		
		//Adds to the distance calculation result, the different position from the difference of the elements
		for(List<PedagogicalSoftwareElementDTO> elements : mapFamilyDifferences.values()) {
			for(PedagogicalSoftwareElementDTO element : elements) {
				diffPosition += element.getElementPosition() + 1;
			}
		}
		
		for (String element : mapElementSimilarities.keySet()) {

			// 4.1- Gets the elements in the aim for this element
			List<PedagogicalSoftwareElementDTO> elementAimElements = aimElements.stream()
					.filter(c -> c.getElementName().equals(element)).collect(Collectors.toList());

			// 4.2- Gets the elements in the origin
			List<PedagogicalSoftwareElementDTO> elementOriginElements = mapElementSimilarities.get(element).stream()
					.map(mes -> mes.clone()).collect(Collectors.toList());

			int nearestPosition = -1;
			int tmpDiff = 0;
			PedagogicalSoftwareElementDTO nearestElement = null;

			// 4.3- For each aim element, we look for the nearest origin element
			for (PedagogicalSoftwareElementDTO aimElement : elementAimElements) {

				if (elementOriginElements.size() > 0) {

					for (PedagogicalSoftwareElementDTO originElement : elementOriginElements) {

						tmpDiff = Math.abs(aimElement.getElementPosition() - originElement.getElementPosition());
						if (nearestPosition == -1) {
							nearestPosition = tmpDiff;
							nearestElement = originElement;
						} else if (nearestPosition > tmpDiff) {
							nearestPosition = tmpDiff;
							nearestElement = originElement;
						}
					}

					diffPosition += nearestPosition;
					elementOriginElements.remove(nearestElement);

				} else {
					diffPosition += aimElement.getElementPosition() + 1;
				}

				nearestPosition = -1;
			}

		}

		return diffPosition;
	}

	/**
	 * Function to get all the elements in a single list
	 * 
	 * @param element     element to analyze its position
	 * @param elementList cumulative element list
	 * @param position    cumulative position
	 * @return
	 */
	public List<PedagogicalSoftwareElementDTO> getAllElements(PedagogicalSoftwareElement element,
			List<PedagogicalSoftwareElementDTO> elementList, AtomicInteger position) {

		// Adds the element to the list
		elementList.add(new PedagogicalSoftwareElementDTO(element, position.get()));
		position.incrementAndGet();

		int numberOfSubElements = 0;

		// Checks if the element has a nested element
		for (PedagogicalSoftwareElement nestedElement : element.getNested()) {

			// Gets the number of elements of the nested element
			numberOfSubElements = getElementsUnderNode(nestedElement, 0);

			// We add 1 because it's a nested element
			position.incrementAndGet();
			position.getAndAdd(numberOfSubElements);
			elementList = this.getAllElements(nestedElement, elementList, position);
		}

		// Checks if the element has a next element
		if (element.getNext() != null) {

			// Gets the next elements
			elementList = this.getAllElements(element.getNext(), elementList, position);
		}

		return elementList;
	}

	/**
	 * Function to get the number of elements of a node
	 * 
	 * @param element
	 * @param subElements
	 * @return
	 */
	private int getElementsUnderNode(PedagogicalSoftwareElement element, int subElements) {

		// 1- Counts all the nested elements in the subtree
		if (element.getNested().size() > 0) {
			for (PedagogicalSoftwareElement nestedElement : element.getNested()) {
				subElements++;
				subElements = getElementsUnderNode(nestedElement, subElements);
			}
		}

		// 2- Counts all the next elements in the subtree
		if (element.getNext() != null) {
			subElements++;
			subElements = getElementsUnderNode(element.getNext(), subElements);
		}

		return subElements;
	}
}

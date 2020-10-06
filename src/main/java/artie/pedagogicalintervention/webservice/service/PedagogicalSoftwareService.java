package artie.pedagogicalintervention.webservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.dto.ResponseBodyDTO;
import artie.pedagogicalintervention.webservice.dto.ResponseDTO;
import artie.pedagogicalintervention.webservice.enums.DistanceEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
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
	 * @param psd
	 */
	public String add(PedagogicalSoftwareData psd) {

		ResponseDTO response = new ResponseDTO(null);
		PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(psd);

		if (objSaved != null) {
			response = new ResponseDTO(new ResponseBodyDTO("OK"));
		}

		return response.toJSON();
	}

	/**
	 * Function to transform a pedagogical software data from string to object
	 * 
	 * @param pse
	 */
	public String add(String psd) {

		ResponseDTO response = new ResponseDTO(null);

		try {

			// 1- Transforms the string into the pedagogical software data
			PedagogicalSoftwareData pedagogicalSoftwareData = new ObjectMapper().readValue(psd,
					PedagogicalSoftwareData.class);

			// 2- Looks for the solution to the exercise
			PedagogicalSoftwareSolution pedagogicalSoftwareSolution = this.pedagogicalSoftwareSolutionService
					.findByExercise(pedagogicalSoftwareData.getExercise());

			// 3- If there at least 1 solution, we get the distances
			if (pedagogicalSoftwareSolution != null) {
				double distance = this.distanceCalculation(pedagogicalSoftwareData, pedagogicalSoftwareSolution);
				pedagogicalSoftwareData.setSolutionDistance(distance);
			}

			PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);

			if (objSaved != null) {
				response = new ResponseDTO(new ResponseBodyDTO("OK"));
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return response.toJSON();
	}

	/**
	 * Distance calculation between an element and its aim
	 * 
	 * @param origin
	 * @param aim
	 * @return
	 */
	public double distanceCalculation(PedagogicalSoftwareData origin, PedagogicalSoftwareSolution aim) {

		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();

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
		diffFamily = this.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, mapFamilyDifferences, diffFamily);

		// 3- Element similarities from the family similarities
		diffElements = this.elementDistanceCalculation(mapFamilySimilarities, mapFamilyDifferences, mapElementSimilarities, aimElements, diffElements);

		// We can now delete the family similarities map
		mapFamilySimilarities.clear();
		mapFamilySimilarities = null;

		// 4- Position similarities from the element similarities
		diffPosition = this.positionDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, diffPosition);

		// 5- Input element similarities from the element similarities
		diffInput = this.inputDistanceCalculation(mapElementSimilarities, mapFamilyDifferences, aimElements, diffInput);

		// 6- Calculates the total distance in base of the coefficients
		totalDistance = (diffFamily / DistanceEnum.FAMILY.getValue()) + (diffElements / DistanceEnum.ELEMENT.getValue())
				+ (diffPosition / DistanceEnum.POSITION.getValue()) + (diffInput / DistanceEnum.INPUT.getValue());

		return totalDistance;
	}

	/**
	 * Function to get the family distance between two elements
	 * 
	 * @param aimElements
	 * @param originElements
	 * @param mapFamilySimilarities
	 * @param mapFamilyDifferences
	 * @param diffFamily
	 * @return
	 */
	public double familyDistanceCalculation(List<PedagogicalSoftwareElementDTO> aimElements,
			List<PedagogicalSoftwareElementDTO> originElements,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences, double diffFamily) {

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
					// If there are no similar families, we count all the elements in the origin +
					// the element in the aim that has not been included in the origin
					diffFamily += 1;
					List<PedagogicalSoftwareElementDTO> tmpFamilyDifferences = aimElements.stream()
							.filter(f -> f.getElementFamily().equals(aimElement.getElementFamily()))
							.collect(Collectors.toList());
					mapFamilyDifferences.put(aimElement.getElementFamily(), tmpFamilyDifferences);

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
	 * @param mapElementDifferences
	 * @param aimElements
	 * @param diffElements
	 * @return
	 */
	public double elementDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences,
			Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities,
			List<PedagogicalSoftwareElementDTO> aimElements, double diffElements) {

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
				}
			}

			// 3.4- Once we got all the aim elements, we check how many elements of this
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
	public double inputDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilyDifferences, List<PedagogicalSoftwareElementDTO> aimElements, double diffInputValues) {
		
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
							}else if(originField.getValue() != aimField.getValue()) {
								accumulatedOriginDifference += 1;
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
	 * @param root        indicates whether the element is situated in the root or
	 *                    not
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

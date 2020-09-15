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
	 * @param psd
	 */
	public String add(PedagogicalSoftwareData psd) {
		
		ResponseDTO response = new ResponseDTO(null);
		PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(psd);
		
		if(objSaved != null) {
			response = new ResponseDTO(new ResponseBodyDTO("OK"));
		}
		
		return response.toJSON();
	}
	
	/**
	 * Function to transform a pedagogical software data from string to object
	 * @param pse
	 */
	public String add(String psd) {
		
		ResponseDTO response = new ResponseDTO(null);
		
		try {
			
			//1- Transforms the string into the pedagogical software data
			PedagogicalSoftwareData pedagogicalSoftwareData = new ObjectMapper().readValue(psd, PedagogicalSoftwareData.class);
			
			//2- Looks for the solution to the exercise
			PedagogicalSoftwareSolution pedagogicalSoftwareSolution = this.pedagogicalSoftwareSolutionService.findByExercise(pedagogicalSoftwareData.getExercise());
			
			//3- If there at least 1 solution, we get the distances
			if(pedagogicalSoftwareSolution != null) {
				double distance = this.distanceCalculation(pedagogicalSoftwareData, pedagogicalSoftwareSolution);
				pedagogicalSoftwareData.setSolutionDistance(distance);
			}
			
			PedagogicalSoftwareData objSaved = this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);
			
			if(objSaved != null) {
				response = new ResponseDTO(new ResponseBodyDTO("OK"));
			}
			
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return response.toJSON();
	}
	
	/**
	 * Distance calculation between an element and its aim
	 * @param origin
	 * @param aim
	 * @return
	 */
	public double distanceCalculation(PedagogicalSoftwareData origin, PedagogicalSoftwareSolution aim) {
		
		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();
		
		//Family variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		double diffFamily = 0;
		
		//Element variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities = new HashMap<>();
		double diffElements = 0;
		
		//Position variables
		double diffPosition = 0;
		
		//Input values variables
		double diffInput = 0;
		
		//total distance
		double totalDistance = 0;
		
		
		//1- Getting all the elements in a single list (not nested)
		for(PedagogicalSoftwareElement element : aim.getElements()) {
			aimElements = this.getAllElements(element, aimElements, new AtomicInteger(0));
		}
		for(PedagogicalSoftwareElement element : origin.getElements()) {
			originElements = this.getAllElements(element, originElements, new AtomicInteger(0));
			
		}
		
		//2- Family differences and similarities
		diffFamily = this.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, diffFamily);
		
		//3- Element similarities from the family similarities
		diffElements = this.elementDistanceCalculation(mapFamilySimilarities, mapElementSimilarities, aimElements, diffElements);
		
		//We can now delete the family similarities map
		mapFamilySimilarities.clear();
		mapFamilySimilarities = null;
		
		//4- Position similarities from the element similarities
		diffPosition = this.positionDistanceCalculation(mapElementSimilarities, aimElements, originElements, diffPosition);
		
		//5- Input element similarities from the element similarities
		diffInput = this.inputDistanceCalculation(mapElementSimilarities, aimElements, originElements, diffInput);
		
		//6- Calculates the total distance in base of the coefficients
		totalDistance = (diffFamily/DistanceEnum.FAMILY.getValue()) + (diffElements/DistanceEnum.ELEMENT.getValue()) + (diffPosition/DistanceEnum.POSITION.getValue()) + (diffInput/DistanceEnum.INPUT.getValue());
		
		return totalDistance;	
	}
	
	
	/**
	 * Function to get the family distance between two elements
	 * @param aimElements
	 * @param originElements
	 * @param mapFamilySimilarities
	 * @param diffFamily
	 * @return
	 */
	public double familyDistanceCalculation(List<PedagogicalSoftwareElementDTO> aimElements, List<PedagogicalSoftwareElementDTO> originElements, Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities, double diffFamily) {
		
		List<String> listFamilyDifferences = new ArrayList<>();
		
		//Checks from the aim side
		for(PedagogicalSoftwareElementDTO aimElement : aimElements) {
			
			//2.1- Checks that this family has not been already checked
			if(!mapFamilySimilarities.containsKey(aimElement.getElementFamily()) && !listFamilyDifferences.contains(aimElement.getElementFamily())) {
				
				//2.1.1- Counts the number of elements of this family existing in the origin
				long countOriginFamilies = originElements
							  					.stream()
							  					.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily()))
							  					.count();
				//2.1.2- Adds to the family result
				if(countOriginFamilies==0) {
					//If there are no similar families, we count all the elements in the origin + the element in the aim that has not been included in the origin
					diffFamily += 1;
					listFamilyDifferences.add(aimElement.getElementFamily());
					
				}else {
					//If there are similarities, we add these similarities to the family map
					List<PedagogicalSoftwareElementDTO> existingElements = originElements
															  					.stream()
															  					.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily()))
															  					.collect(Collectors.toList());
					mapFamilySimilarities.put(aimElement.getElementFamily(), existingElements);
				}
			}
		}
		
		//Checks from the origin side
		for(PedagogicalSoftwareElementDTO originElement : originElements) {
			
			//3.1- Checks that this family has not been already checked
			if(!mapFamilySimilarities.containsKey(originElement.getElementFamily()) && !listFamilyDifferences.contains(originElement.getElementFamily())) {
				
				//3.1.1- Counts the number of elements of this family existing in the origin
				long countAimFamilies = aimElements
						  					.stream()
						  					.filter(c -> c.getElementFamily().equals(originElement.getElementFamily()))
						  					.count();
				//3.1.2- Adds to the family result
				if(countAimFamilies==0) {
					//If there are no similar families, we count all the elements in the origin + the element in the aim that has not been included in the origin
					diffFamily += 1;
					listFamilyDifferences.add(originElement.getElementFamily());
					
				}
			}
		}
		
		return diffFamily;
	}
	
	/**
	 * Function to get the element distance between two elements
	 * @param mapFamilySimilarities
	 * @param mapElementSimilarities
	 * @param aimElements
	 * @param diffElements
	 * @return
	 */
	public double elementDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities, Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, List<PedagogicalSoftwareElementDTO> aimElements, double diffElements) {
		
		
		//For the similar families
		for(String family : mapFamilySimilarities.keySet()) {
			
			//3.1- Gets the elements in the aim for this family
			List<PedagogicalSoftwareElementDTO> familyAimElements = aimElements
																		.stream()
																		.filter(c -> c.getElementFamily().equals(family))
																		.collect(Collectors.toList());
			//3.2- Gets the elements in the origin for this family
			List<PedagogicalSoftwareElementDTO> familyOriginElements = mapFamilySimilarities.get(family);
		
			
			//3.3- For each aim element we look for the origin element
			for(PedagogicalSoftwareElementDTO familyAimElement : familyAimElements) {
				
				//3.3.1- Counts how many aim elements are the same element
				long countAimElements = familyAimElements
													.stream()
													.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
													.count();
				
				//3.3.2 - Counts the number of elements similar to the aim element for the family
				long countOriginElements = familyOriginElements
													.stream()
													.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
													.count();
				
				diffElements += Math.abs(countAimElements - countOriginElements);				
				
				//3.3.3- Adds to the element result
				if(countOriginElements > 0) {
					
					List<PedagogicalSoftwareElementDTO> existingElements = familyOriginElements
																				.stream()
																				.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
																				.collect(Collectors.toList());
					
					//Once the elements have been added to the map, we delete them from the list to avoid repeat them
					familyOriginElements.removeAll(existingElements);
					
					//If there are similarities, we add these similarities to the element map
					mapElementSimilarities.put(familyAimElement.getElementName(), existingElements);
				}
			}
			
			//3.4- Once we got all the aim elements, we check how many elements of this family remain in the origin
			diffElements += familyOriginElements.size();
		}
		
		return diffElements;
	}
	
	
	/**
	 * Function to get the input distance between the inputs from the same elements
	 * @param mapElementSimilarities
	 * @param aimElements
	 * @param originElements
	 * @param diffInputValues
	 * @return
	 */
	public double inputDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, List<PedagogicalSoftwareElementDTO> aimElements, List<PedagogicalSoftwareElementDTO> originElements, double diffInputValues) {
		
		for(String element : mapElementSimilarities.keySet()) {
			
			//5.1- Gets the elements in the aim for this element
			List<PedagogicalSoftwareElementDTO> elementAimElements = aimElements
																		.stream()
																		.filter(c -> c.getElementName().equals(element))
																		.collect(Collectors.toList());
			
			//5.2- Gets the elements in the origin
			List<PedagogicalSoftwareElementDTO> elementOriginElements = mapElementSimilarities.get(element);
			
			//5.3- Checks all the inputs for each element
			for(PedagogicalSoftwareElementDTO elementAimElement : elementAimElements) {
				
				for(int input=0; input<elementAimElement.getInputs().size(); input++) {
					for(int field=0; field<elementAimElement.getInputs().get(input).getFields().size(); field++) {
					
						for(PedagogicalSoftwareElementDTO elementOriginElement : elementOriginElements) {
							
							PedagogicalSoftwareField solutionField = elementAimElement.getInputs().get(input).getFields().get(field);
							PedagogicalSoftwareField workspaceField = elementOriginElement.getInputs().get(input).getFields().get(field);
							
							//If the field of the input of the aim element is equal of the field of the input of the origin element and the elements are the same short
							if(!solutionField.equals(workspaceField) && elementOriginElement.getElementName().equals(elementAimElement.getElementName())) {
								
								if(solutionField.isNumeric()) {
									
									double difference = Math.abs(solutionField.getDoubleValue() - workspaceField.getDoubleValue());
									double ratio = difference / solutionField.getDoubleValue();
									diffInputValues += ratio;
									
 								}else {
									diffInputValues += 1;
								}
							}							
						}
						
					}
				}
				
			}		
			
		}
		
		return diffInputValues;
	}
	
	
	/**
	 * Function to calculate the distance between the positions
	 * @param mapElementSimilarities
	 * @param aimElements
	 * @param originElements
	 * @param diffPosition
	 * @return
	 */
	public double positionDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, List<PedagogicalSoftwareElementDTO> aimElements, List<PedagogicalSoftwareElementDTO> originElements, double diffPosition) {
		
		long nearestPosition= -1;
		
		for(String element : mapElementSimilarities.keySet()) {
			
			
			//4.1- Gets the elements in the aim for this element
			List<PedagogicalSoftwareElementDTO> elementAimElements = aimElements
																		.stream()
																		.filter(c -> c.getElementName().equals(element))
																		.collect(Collectors.toList());
	
			//4.2- Gets the elements in the origin
			List<PedagogicalSoftwareElementDTO> elementOriginElements = mapElementSimilarities.get(element);
			
			//4.3- From the correct elements we get the less distance in the position between then aim and the origin
			for(PedagogicalSoftwareElementDTO elementAimElement : elementAimElements) {
				
				nearestPosition = -1;
				
				for(PedagogicalSoftwareElementDTO elementOriginElement : elementOriginElements) {
					
					if(nearestPosition == -1) {
						nearestPosition = Math.abs(elementAimElement.getElementPosition() - elementOriginElement.getElementPosition());
					}else if (nearestPosition > Math.abs(elementAimElement.getElementPosition() - elementOriginElement.getElementPosition())) {
						nearestPosition = Math.abs(elementAimElement.getElementPosition() - elementOriginElement.getElementPosition());
					}
				}
				
				if(nearestPosition > -1) {
					diffPosition += nearestPosition;
				}
			}
		}
		
		return diffPosition;
	}
	
	
	
	/**
	 * Function to get all the elements in a single list
	 * @param element element to analyze its position
	 * @param elementList cumulative element list
	 * @param position cumulative position
	 * @param root indicates whether the element is situated in the root or not
	 * @return
	 */
	public List<PedagogicalSoftwareElementDTO> getAllElements(PedagogicalSoftwareElement element, List<PedagogicalSoftwareElementDTO> elementList, AtomicInteger position){
		
		//Adds the element to the list
		elementList.add(new PedagogicalSoftwareElementDTO(element, position.get()));
		position.incrementAndGet();
		
		int numberOfSubElements = 0;
		
		//Checks if the element has a nested element
		for(PedagogicalSoftwareElement nestedElement : element.getNested()) {
			
			//Gets the number of elements of the nested element
			numberOfSubElements = getElementsUnderNode(nestedElement, 0);
			
			//We add 1 because it's a nested element
			position.incrementAndGet();
			position.getAndAdd(numberOfSubElements);
			elementList = this.getAllElements(nestedElement, elementList, position);
		}
		
		//Checks if the element has a next element
		if(element.getNext() != null) {
			
			//Gets the next elements
			elementList = this.getAllElements(element.getNext(), elementList, position);
		}
			
		return elementList;
	}
	
	/**
	 * Function to get the number of elements of a node
	 * @param element
	 * @param subElements
	 * @return
	 */
	private int getElementsUnderNode(PedagogicalSoftwareElement element, int subElements) {
		
		//1- Counts all the nested elements in the subtree
		if(element.getNested().size() > 0) {
			for(PedagogicalSoftwareElement nestedElement : element.getNested()) {
				subElements++;
				subElements = getElementsUnderNode(nestedElement, subElements);
			}
		}
		
		//2- Counts all the next elements in the subtree
		if(element.getNext() != null) {
			subElements++;
			subElements = getElementsUnderNode(element.getNext(), subElements);	
		}
		
		return subElements;
	}
}

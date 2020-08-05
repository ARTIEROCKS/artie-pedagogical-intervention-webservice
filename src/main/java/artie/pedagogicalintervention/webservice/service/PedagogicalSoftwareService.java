package artie.pedagogicalintervention.webservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.dto.PedagogicalSoftwareElementDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;

@Service
public class PedagogicalSoftwareService {

	@Autowired
	private PedagogicalSoftwareDataRepository pedagogicalSoftwareDataRepository;
	
	
	/**
	 * Function to add the pedagogical software data in the database
	 * @param psd
	 */
	public void add(PedagogicalSoftwareData psd) {
		this.pedagogicalSoftwareDataRepository.save(psd);
	}
	
	/**
	 * Function to transform a pedagogical software data from string to object
	 * @param pse
	 */
	public void add(String pse) {
		try {
			
			//Transforming all the elements to pedagogical software elements
			PedagogicalSoftwareElement[] pedagogicalSoftwareElements = new ObjectMapper().readValue(pse, PedagogicalSoftwareElement[].class);
			
			//Inserting all these elements in the pedagogical software data block
			PedagogicalSoftwareData pedagogicalSoftwareData = new PedagogicalSoftwareData();
			for (PedagogicalSoftwareElement pedagogicalSoftwareElement : pedagogicalSoftwareElements){
				
				pedagogicalSoftwareData.addElement(pedagogicalSoftwareElement);
				this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Distance calculation between an element and its aim
	 * @param origin
	 * @param aim
	 * @return
	 */
	public double distanceCalculation(PedagogicalSoftwareData origin, PedagogicalSoftwareData aim) {
		
		List<PedagogicalSoftwareElementDTO> aimElements = new ArrayList<>();
		List<PedagogicalSoftwareElementDTO> originElements = new ArrayList<>();
		
		//Family variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities = new HashMap<>();
		long diffFamily = 0;
		
		//Element variables
		Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities = new HashMap<>();
		long diffElements = 0;
		
		//Input values variables
		long diffInputValues = 0;
		
		
		//1- Getting all the elements in a single list (not nested)
		for(PedagogicalSoftwareElement element : aim.getElements()) {
			aimElements = this.getAllElements(element, aimElements, 0);
		}
		for(PedagogicalSoftwareElement element : origin.getElements()) {
			originElements = this.getAllElements(element, originElements, 0);
			
		}
		
		//2- Family differences and similarities
		diffFamily = this.familyDistanceCalculation(aimElements, originElements, mapFamilySimilarities, diffFamily);
		
		//3- Element similarities from the family similarities
		diffElements = this.elementDistanceCalculation(mapFamilySimilarities, mapElementSimilarities, aimElements, diffElements);
		
		//We can now delete the family similarities map
		mapFamilySimilarities.clear();
		mapFamilySimilarities = null;
		
		//4- Input element similarities from the element similarities
		diffInputValues = this.inputDistanceCalculation(mapElementSimilarities, aimElements, originElements, diffInputValues);
		
		return 0;	
	}
	
	
	/**
	 * Function to get the family distance between two elements
	 * @param aimElements
	 * @param originElements
	 * @param mapFamilySimilarities
	 * @param diffFamily
	 * @return
	 */
	public long familyDistanceCalculation(List<PedagogicalSoftwareElementDTO> aimElements, List<PedagogicalSoftwareElementDTO> originElements, Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities, long diffFamily) {
		
		boolean addedDifference = false;
		int addedElements = 0;
		
		for(PedagogicalSoftwareElementDTO aimElement : aimElements) {
			
			//2.1- Checks that this family has not been already checked
			if(!mapFamilySimilarities.containsKey(aimElement.getElementFamily())) {
				
				//2.1.1- Counts the number of elements of this family exist in the origin
				long countOriginFamilies = originElements
							  					.stream()
							  					.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily()))
							  					.count();
				//2.1.2- Adds to the family result
				if(countOriginFamilies==0) {
					//If there are no similar families, we count all the elements in the origin
					diffFamily += originElements.size();
					addedDifference = true;
				}else {
					//If there are similarities, we add these similarities to the family map
					List<PedagogicalSoftwareElementDTO> existingElements = originElements
															  					.stream()
															  					.filter(c -> c.getElementFamily().equals(aimElement.getElementFamily()))
															  					.collect(Collectors.toList());
					mapFamilySimilarities.put(aimElement.getElementFamily(), existingElements);
					addedElements += existingElements.size();
				}
			}
		}
		
		if(addedDifference) {
			//We return the distance of the families, less the number of families found
			diffFamily -= addedElements;
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
	public long elementDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapFamilySimilarities, Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, List<PedagogicalSoftwareElementDTO> aimElements, long diffElements) {
		
		boolean addedDifference = false;
		int addedElements = 0;
		
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
				
				//3.3.3- Adds to the element result
				if(countOriginElements == 0) {
					//If there are no similar elements, we count all the elements of the family in the origin
					diffElements += familyOriginElements.size();
					addedDifference = true;
				} else {
					
					List<PedagogicalSoftwareElementDTO> existingElements = familyOriginElements
																				.stream()
																				.filter(c -> c.getElementName().equals(familyAimElement.getElementName()))
																				.collect(Collectors.toList());
					
					//Checks the difference in number between the origin and the aim
					long differenceOriginAim = Math.abs(countAimElements - countOriginElements);
					
					//If there are similarities, we add these similarities to the element map
					mapElementSimilarities.put(familyAimElement.getElementName(), existingElements);
					addedElements += existingElements.size() - differenceOriginAim;
				}
			}
		}
		
		if(addedDifference) {
			//We return the distance of the elements, less the number of elements found
			diffElements -= addedElements;
		}
		
		return diffElements;
	}
	
	
	private long inputDistanceCalculation(Map<String, List<PedagogicalSoftwareElementDTO>> mapElementSimilarities, List<PedagogicalSoftwareElementDTO> aimElements, List<PedagogicalSoftwareElementDTO> originElements, long diffInputValues) {
		
		
		for(String element : mapElementSimilarities.keySet()) {
			
			//4.1- Gets the elements in the aim for this element
			List<PedagogicalSoftwareElementDTO> elementAimElements = aimElements
																		.stream()
																		.filter(c -> c.getElementName().equals(element))
																		.collect(Collectors.toList());
			
			//4.2- Gets the elements in the origin
			List<PedagogicalSoftwareElementDTO> elementOriginElements = mapElementSimilarities.get(element);
						
			
		}
		
		return diffInputValues;
	}
	
	
	/**
	 * Function to get all the elements in a single list
	 * @param element
	 * @param elementList
	 * @param order
	 * @return
	 */
	public List<PedagogicalSoftwareElementDTO> getAllElements(PedagogicalSoftwareElement element, List<PedagogicalSoftwareElementDTO> elementList, int order){
		
		//Adds the element to the list
		elementList.add(new PedagogicalSoftwareElementDTO(element, order));
		order++;
		
		//Checks if the element has a next element
		if(element.getNext() != null) {
			elementList = this.getAllElements(element.getNext(), elementList, order);
		}
			
		return elementList;
	}
	

}

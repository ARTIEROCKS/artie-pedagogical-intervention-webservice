package artie.pedagogicalintervention.webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
}

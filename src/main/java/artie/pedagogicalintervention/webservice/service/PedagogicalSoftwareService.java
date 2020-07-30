package artie.pedagogicalintervention.webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
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
	 * @param psd
	 */
	public void add(String psd) {
		try {
			PedagogicalSoftwareData pedagogicalSoftwareData = new ObjectMapper().readValue(psd, PedagogicalSoftwareData.class);
			this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}

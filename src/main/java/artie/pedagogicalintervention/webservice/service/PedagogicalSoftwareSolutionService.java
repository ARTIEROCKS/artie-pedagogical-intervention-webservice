package artie.pedagogicalintervention.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareSolutionRepository;

@Service
public class PedagogicalSoftwareSolutionService {
	
	@Autowired
	private PedagogicalSoftwareSolutionRepository pedagogicalSoftwareSolutionRepository;
	
	/**
	 * Function to add the pedagogical software solution in the database
	 * @param psd
	 */
	public void add(PedagogicalSoftwareSolution pss) {
		this.pedagogicalSoftwareSolutionRepository.save(pss);
	}
	
	/**
	 * Function to transform a pedagogical software solution from string to object
	 * @param pse
	 */
	public void add(String pse) {
		try {
			
			//Transforming all the elements to pedagogical software elements
			PedagogicalSoftwareElement[] pedagogicalSoftwareElements = new ObjectMapper().readValue(pse, PedagogicalSoftwareElement[].class);
			
			//Inserting all these elements in the pedagogical software data block
			PedagogicalSoftwareSolution pedagogicalSoftwareSolution = new PedagogicalSoftwareSolution();
			for (PedagogicalSoftwareElement pedagogicalSoftwareElement : pedagogicalSoftwareElements){
				pedagogicalSoftwareSolution.addElement(pedagogicalSoftwareElement);
				this.pedagogicalSoftwareSolutionRepository.save(pedagogicalSoftwareSolution);
			}
			
		}catch(JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Function to find the solution of an exercise
	 * @param exercise
	 * @return
	 */
	public PedagogicalSoftwareSolution findByExercise(String exercise) {
		List<PedagogicalSoftwareSolution> solutions = this.pedagogicalSoftwareSolutionRepository.findByExercise(exercise);
		return (solutions.size() > 0 ? solutions.get(0) : null);
	}

}

package artie.pedagogicalintervention.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.dto.ResponseBodyDTO;
import artie.pedagogicalintervention.webservice.dto.ResponseDTO;
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
	public String add(PedagogicalSoftwareSolution pss) {
		
		ResponseDTO response = new ResponseDTO(null);
		PedagogicalSoftwareSolution objSaved = this.pedagogicalSoftwareSolutionRepository.save(pss);
		
		if(objSaved != null) {
			response = new ResponseDTO(new ResponseBodyDTO("OK"));
		}
		
		return response.toJSON();
	}
	
	/**
	 * Function to transform a pedagogical software solution from string to object
	 * @param pse
	 */
	public String add(String pse) {
		
		ResponseDTO response = new ResponseDTO(null);
		
		try {					
			//1- Transforms the string in pedagogical software solution object
			PedagogicalSoftwareSolution pedagogicalSoftwareSolution = new ObjectMapper().readValue(pse, PedagogicalSoftwareSolution.class);
			
			//2- Searches if there is a solution for this exercise
			List<PedagogicalSoftwareSolution> pedagogicalSoftwareSolutions = this.pedagogicalSoftwareSolutionRepository.findByExerciseIdAndUserId(pedagogicalSoftwareSolution.getExercise().getId(), pedagogicalSoftwareSolution.getUserId());
			
			//3- If there is an existing pedagogical software solution, we update its data
			if(pedagogicalSoftwareSolutions.size() > 0 ) {
				PedagogicalSoftwareSolution pedagogicalSoftwareSolutionDb = pedagogicalSoftwareSolutions.get(0);
				pedagogicalSoftwareSolutionDb.setElements(pedagogicalSoftwareSolution.getElements());
				pedagogicalSoftwareSolutionDb.setScreenShot(pedagogicalSoftwareSolution.getScreenShot());
				PedagogicalSoftwareSolution objSaved = this.pedagogicalSoftwareSolutionRepository.save(pedagogicalSoftwareSolutionDb);
				
				if(objSaved != null) {
					response = new ResponseDTO(new ResponseBodyDTO("OK"));
				}
			}else {
				this.pedagogicalSoftwareSolutionRepository.save(pedagogicalSoftwareSolution);
			}
		}catch(JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return response.toJSON();
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
	
	/**
	 * Function to find the solution of an exercise and an user
	 * @param exercise
	 * @param userId
	 * @return
	 */
	public PedagogicalSoftwareSolution findByExerciseAndUserId(Exercise exercise, String userId) {
		List<PedagogicalSoftwareSolution> solutions = this.pedagogicalSoftwareSolutionRepository.findByExerciseIdAndUserId(exercise.getId(), userId);
		return (solutions.size() > 0 ? solutions.get(0) : null);
	}

}

package artie.pedagogicalintervention.webservice.service;

import java.util.List;

import artie.common.web.enums.ValidSolutionEnum;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareDataRepository;
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

	@Autowired
	private PedagogicalSoftwareDataRepository pedagogicalSoftwareDataRepository;
	
	/**
	 * Function to add the pedagogical software solution in the database
	 * @param pss
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
	 * Function to create a solution in base of a software data Id
	 * @param pedagogicalSoftwareDataId
	 */
	public void addFromPedagogicalSoftwareDataId(String pedagogicalSoftwareDataId){

		PedagogicalSoftwareData pedagogicalSoftwareData = this.pedagogicalSoftwareDataRepository.findById(pedagogicalSoftwareDataId).orElse(null);

		//If the pedagogical software data has been retrieved
		if(pedagogicalSoftwareData != null){
			PedagogicalSoftwareSolution pedagogicalSoftwareSolution = new PedagogicalSoftwareSolution(pedagogicalSoftwareData.getStudent().getUserId(),
																										pedagogicalSoftwareData.getId(),
																										pedagogicalSoftwareData.getExercise(),
																										pedagogicalSoftwareData.getScreenShot(),
																										pedagogicalSoftwareData.getElements());
			//Save the pedagogical software solution in the database
			this.pedagogicalSoftwareSolutionRepository.save(pedagogicalSoftwareSolution);
		}
	}

	/**
	 * Function to delete a solution in base of a software data Id
	 * @param pedagogicalSoftwareDataId
	 */
	public void deleteFromPedagogicalSoftwareDataId(String pedagogicalSoftwareDataId){

		//1- Finds all the solutions in base of the pedagogical software data (there must be just 1)
		List<PedagogicalSoftwareSolution> pedagogicalSoftwareSolutions = this.pedagogicalSoftwareSolutionRepository.findByPedagogicalSoftwareDataId(pedagogicalSoftwareDataId);

		//2- Deletes all the solutions in base of the pedagogical software data found
		this.pedagogicalSoftwareSolutionRepository.deleteAll(pedagogicalSoftwareSolutions);
	}

	/**
	 * Function to delete a solution and update its related software data in case that it exists
	 * @param solutionId
	 */
	public void deleteSolutionById(String solutionId){

		//1- Finds the solution in the database to know if the solution comes from the validation of an exercise
		PedagogicalSoftwareSolution solution = this.pedagogicalSoftwareSolutionRepository.findById(solutionId).orElse(null);

		if(solution != null){
			//2- Checks if the solution comes from the validation of an exercise and we invalidate it
			if(solution.getPedagogicalSoftwareDataId() != null && solution.getPedagogicalSoftwareDataId() != ""){

				PedagogicalSoftwareData pedagogicalSoftwareData = this.pedagogicalSoftwareDataRepository.findById(solution.getPedagogicalSoftwareDataId()).orElse(null);
				if(pedagogicalSoftwareData != null){
					pedagogicalSoftwareData.setValidSolution(ValidSolutionEnum.REJECTED.getValue());
					this.pedagogicalSoftwareDataRepository.save(pedagogicalSoftwareData);
				}
			}

			//3- Deletes the solution from the database
			this.pedagogicalSoftwareSolutionRepository.delete(solution);
		}
	}

	/**
	 * Function to return all the solutions of an user Id
	 * @param userId
	 * @return
	 */
	public List<PedagogicalSoftwareSolution> findByUserId(String userId){
		return this.pedagogicalSoftwareSolutionRepository.findByUserId(userId);
	}
	
	/**
	 * Function to find the solution of an exercise and an user
	 * @param exercise
	 * @param userId
	 * @return
	 */
	public List<PedagogicalSoftwareSolution> findByExerciseAndUserId(Exercise exercise, String userId) {
		return this.pedagogicalSoftwareSolutionRepository.findByExerciseIdAndUserId(exercise.getId(), userId);
	}


}

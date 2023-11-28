package artie.pedagogicalintervention.webservice.model;

import artie.common.web.dto.Exercise;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection="PedagogicalSoftwareSolution")
public class  PedagogicalSoftwareSolution {
	
	@Id
	private String id;
	private String userId;
	private String pedagogicalSoftwareDataId;
	private String exerciseId;
	private Exercise exercise;
	private String screenShot;
	private String binary;
	private List<PedagogicalSoftwareElement> elements = new ArrayList<>();
	private double maximumDistance;

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
		
		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}
	/**
	 * Function to get all the blocks from inside the elements
	 * @return
	 */
	public List<PedagogicalSoftwareBlock> getAllBlocks(){
		List<PedagogicalSoftwareBlock> allBlocks = new ArrayList<>();
		this.elements.forEach(e -> allBlocks.addAll(e.getBlocks()));
		return allBlocks;
	}
	

	
	/**
	 * Parameterized constructor
	 * @param exercise
	 * @param elements
	 */
	public PedagogicalSoftwareSolution(Exercise exercise, List<PedagogicalSoftwareElement> elements) {
		this.exercise = exercise;
		this.elements = elements;
		
		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}

	/**
	 * Parameterized constructor
	 * @param userId
	 * @param pedagogicalSoftwareDataId
	 * @param exercise
	 * @param screenShot
	 * @param binary
	 * @param elements
	 * @param maximumDistance
	 */
	public PedagogicalSoftwareSolution(String userId, String pedagogicalSoftwareDataId,  Exercise exercise, String screenShot, String binary, List<PedagogicalSoftwareElement> elements, double maximumDistance){
		this.userId=userId;
		this.pedagogicalSoftwareDataId = pedagogicalSoftwareDataId;
		this.exercise = exercise;
		this.screenShot = screenShot;
		this.binary = binary;
		this.elements = elements;
		this.maximumDistance = maximumDistance;

		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}

	public void addElement(PedagogicalSoftwareElement element) {
		this.elements.add(element);
	}
}

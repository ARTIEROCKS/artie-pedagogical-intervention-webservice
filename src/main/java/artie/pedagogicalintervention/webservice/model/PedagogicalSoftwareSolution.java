package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import artie.common.web.dto.Exercise;

@Document(collection="PedagogicalSoftwareSolution")
public class PedagogicalSoftwareSolution {
	
	@Id
	private String id;
	private String userId;
	private String pedagogicalSoftwareDataId;
	private String exerciseId;
	private Exercise exercise;
	private String screenShot;
	private List<PedagogicalSoftwareElement> elements = new ArrayList<>();
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPedagogicalSoftwareDataId(){return this.pedagogicalSoftwareDataId;}
	public void setPedagogicalSoftwareDataId(String pedagogicalSoftwareDataId){this.pedagogicalSoftwareDataId=pedagogicalSoftwareDataId;}
	public String getExerciseId() {
		return this.exerciseId;
	}
	public void setExerciseId(String exerciseId) {
		this.exerciseId = exerciseId;
	}
	public Exercise getExercise() {
		return this.exercise;
	}
	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
		
		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}
	public String getScreenShot() {
		return this.screenShot;
	}
	public void setScreenShot(String screenShot) {
		this.screenShot = screenShot;
	}
	public List<PedagogicalSoftwareElement> getElements(){
		return this.elements;
	}
	public void setElements(List<PedagogicalSoftwareElement> elements) {
		this.elements = elements;
	}
	
	/**
	 * Default Constructor
	 */
	public PedagogicalSoftwareSolution() {}
	
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
	 * @param elements
	 */
	public PedagogicalSoftwareSolution(String userId, String pedagogicalSoftwareDataId,  Exercise exercise, String screenShot, List<PedagogicalSoftwareElement> elements){
		this.userId=userId;
		this.pedagogicalSoftwareDataId = pedagogicalSoftwareDataId;
		this.exercise = exercise;
		this.screenShot = screenShot;
		this.elements = elements;

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

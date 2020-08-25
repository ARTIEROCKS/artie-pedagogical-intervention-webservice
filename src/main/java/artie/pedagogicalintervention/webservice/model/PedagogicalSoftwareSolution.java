package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="PedagogicalSoftwareSolution")
public class PedagogicalSoftwareSolution {
	
	@Id
	private String id;
	private String exercise;
	private List<PedagogicalSoftwareElement> elements = new ArrayList<>();
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExercise() {
		return this.exercise;
	}
	public void setExercise(String exercise) {
		this.exercise = exercise;
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
	public PedagogicalSoftwareSolution(String exercise, List<PedagogicalSoftwareElement> elements) {
		this.exercise = exercise;
		this.elements = elements;
	}
	
	public void addElement(PedagogicalSoftwareElement element) {
		this.elements.add(element);
	}
}

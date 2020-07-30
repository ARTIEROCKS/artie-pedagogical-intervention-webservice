package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="PedagogicalSoftwareData")
public class PedagogicalSoftwareData {
	
	@Id
	private String id;
	private List<PedagogicalSoftwareElement> elements = new ArrayList<>();
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
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
	public PedagogicalSoftwareData() {}
	
	/**
	 * Parameterized constructor
	 * @param elements
	 */
	public PedagogicalSoftwareData(List<PedagogicalSoftwareElement> elements) {
		this.elements = elements;
	}
}

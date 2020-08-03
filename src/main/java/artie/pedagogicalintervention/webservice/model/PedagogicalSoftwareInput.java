package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class PedagogicalSoftwareInput {

	private String name;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PedagogicalSoftwareField> getFields() {
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	
	/**
	 * Default constructor
	 */
	public PedagogicalSoftwareInput() {}
	
	/**
	 * Parameterized constructor
	 * @param name
	 * @param fields
	 */
	public PedagogicalSoftwareInput(String name, List<PedagogicalSoftwareField> fields) {
		super();
		this.name = name;
		this.fields = fields;
	}

}
